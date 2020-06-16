// Copyright (c) 2020, Oracle Corporation and/or its affiliates.

package org.books.bobby;

import com.tangosol.net.ConfigurableCacheFactory;

public interface Loader {
    void load(ConfigurableCacheFactory ccf);
}
