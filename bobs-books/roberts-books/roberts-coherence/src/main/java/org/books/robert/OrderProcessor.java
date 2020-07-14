// Copyright (c) 2020, Oracle and/or its affiliates.
// Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracle.com/licenses/upl.

package org.books.robert;

import java.io.IOException;
import java.util.Optional;
import java.util.Set;

import com.tangosol.net.CacheFactory;
import com.tangosol.net.cache.BinaryEntryStore;
import com.tangosol.util.Base;
import com.tangosol.util.BinaryEntry;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

/**
 * Submits buffered orders to the backend.
 */
public class OrderProcessor implements BinaryEntryStore<String, String> {
    private String orderManagerEndpoint;

    public OrderProcessor() {
        String hostname = Optional.ofNullable(System.getenv("BACKEND_HOSTNAME")).orElse("localhost");
        String port = Optional.ofNullable(System.getenv("BACKEND_PORT")).orElse("8080");
        orderManagerEndpoint = Optional.ofNullable(System.getenv("ORDER_MANAGER_URL"))
                .orElse("http://" + hostname + ":" + port + "/bobs-bookstore-order-manager/order");
    }

    @Override
    public void load(BinaryEntry<String, String> binaryEntry) {
        // write-only, so there is nothing to load
    }

    @Override
    public void loadAll(Set set) {
        // no-op
    }

    @Override
    public void erase(BinaryEntry<String, String> binaryEntry) {
        // no-op
    }

    @Override
    public void eraseAll(Set<? extends BinaryEntry<String, String>> set) {
        // no-op
    }

    @Override
    public void store(BinaryEntry<String, String> binaryEntry) {
        // Log order submission (for alerting)
        final String jsonOrder = binaryEntry.getValue();

        try (CloseableHttpClient client = HttpClients.createDefault()) {
            // POST order to Bob's Backend
            if (submitOrder(client, jsonOrder)) {
                // order was submitted successfully, remove it from the cache
                binaryEntry.remove(false);
                CacheFactory.log("New order: " + jsonOrder, Base.LOG_INFO);
            }
        }
        catch (IOException e) {
            CacheFactory.log("Failed to submit order: " + e.getMessage(), Base.LOG_WARN);
        }
    }

    @Override
    public void storeAll(Set<? extends BinaryEntry<String, String>> set) {
        set.forEach(this::store);
    }

    private boolean submitOrder(HttpClient client, String jsonOrder) throws IOException {
        HttpPost request = new HttpPost(orderManagerEndpoint);
        request.setHeader("Content-Type", "application/json");

        StringEntity entity = new StringEntity(jsonOrder);
        entity.setContentType("application/json");
        request.setEntity(entity);

        HttpResponse response = client.execute(request);
        return response.getStatusLine().getStatusCode() == 204;
    }
}
