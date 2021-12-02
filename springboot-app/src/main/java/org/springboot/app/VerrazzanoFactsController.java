// Copyright (c) 2020, 2021, Oracle and/or its affiliates.
// Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracle.com/licenses/upl.

package org.springboot.app;

import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

@RestController
public class VerrazzanoFactsController {

    String[] verrazzanoFact = new String[] {
        "Verrazzano was eaten by cannibals.\n",
        "The Bridge was about to be named after John F. Kennedy\n",
        "In 1524, Verrazzano became the first European to enter the New York Harbor and the Hudson River.\n",
        "The Verrazzano-Narrows Bridge was the longest suspension bridge in the world at the time of its completion in 1964, surpassing the Golden Gate Bridge by 60 feet.\n",
        "Its monumental 693 foot high towers are 1 5/8 inches farther apart at their tops than at their bases because the 4,260 foot distance between them made it necessary to compensate for the earth’s curvature.\n",
        "Each tower weighs 27,000 tons and is held together with three million rivets and one million bolts.\n",
        "Seasonal contractions and expansions of the steel cables cause the double-decked roadway to be 12 feet lower in the summer than in the winter.\n",
        "The bridge marks the gateway to New York Harbor; all cruise ships and most container ships arriving at the Port of New York and New Jersey must pass underneath the bridge and therefore must be built to accommodate the clearance under the bridge.\n ",
        "In The Avengers, superhero Iron Man flies under, reverses course, and overflies the bridge on the way to intercepting a nuclear missile.\n",
        "The towers can be seen from spots in all five boroughs of New York City and in New Jersey.\n"
    };

    @RequestMapping("/facts")
    public String verrazzanoFact() {
        return verrazzanoFact[(int)(Math.random()*10)];
    }

    @RequestMapping(value = "/externalCall")
    @ResponseBody
    public String externalCall(@RequestParam("inurl") String inurl) throws IOException {
        System.out.println("Accessing passed in Url " + inurl);
        URL url = new URL(inurl);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");
        int status = con.getResponseCode();
        System.out.println("Http Call returned " + String.valueOf(status) );
        return String.valueOf(status);
    }
}
