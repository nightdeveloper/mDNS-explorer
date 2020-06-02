/*
 * The MIT License
 *
 * Copyright (c) 2015-2018 Todd Kulesza <todd@dropline.net>
 *
 * This file is part of Hola.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package com.github.nightdeveloper.mdns_explorer;

import com.github.nightdeveloper.mdns_explorer.dns.Domain;
import com.github.nightdeveloper.mdns_explorer.sd.Instance;
import com.github.nightdeveloper.mdns_explorer.sd.Query;
import com.github.nightdeveloper.mdns_explorer.sd.Service;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * A minimal implementation of mDNS-SD, as described in RFC 6762 and RFC 6763.
 */

public class MdnsExplorer {
    final static Logger logger = LogManager.getLogger(MdnsExplorer.class);

    private static List<InetAddress> getLocalAddresses() throws SocketException {
        Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();

        List<InetAddress> result = new ArrayList<>();
        while (networkInterfaces.hasMoreElements()) {
            for (InterfaceAddress interfaceAddress : networkInterfaces.nextElement().getInterfaceAddresses()) {
                result.add(interfaceAddress.getAddress());
            }
        }

        return result;
    }

    public static void main(String[] args) {

        logger.info("Welcome to mDNS Explorer!");

        try {
            String hostname = InetAddress.getLocalHost().getHostName();

            logger.info("");
            logger.info("Using hostname " + hostname);
            logger.info("");

            List<InetAddress> addresses = getLocalAddresses();

            for (InetAddress address : addresses) {

                logger.debug("querying interface " + address.getHostAddress());

                Service discoveryService = Service.fromName(Service.SERVICE_QUERY);
                Query serviceQuery = Query.createFor(discoveryService, Domain.LOCAL);
                Set<Instance> serviceInstances = serviceQuery.runOnce(address);
                logger.debug("got count " + serviceInstances.size());

                List<String> serviceNames = serviceInstances.stream().map(Instance::getFullName)
                        .sorted()
                        .distinct()
                        .collect(Collectors.toList());


                for (String serviceName : serviceNames) {
                    logger.info("Discovered service " + serviceName + ":");

                    Query instanceQuery = Query.createFor(Service.fromName(serviceName), Domain.LOCAL);
                    Set<Instance> instances = instanceQuery.runOnce(address);

                    instances.forEach(instance -> logger.info(" - ["

                            + instance.getAddresses().stream()
                            .map(inetAddress ->
                                    inetAddress.getHostName().equals(inetAddress.getHostAddress()) ?
                                            inetAddress.getHostAddress() :
                                            inetAddress.getHostName() + " " + inetAddress.getHostAddress())
                            .collect(Collectors.joining(", "))

                            + "] port " + instance.getPort()

                            + (instance.getAttributes().size() > 0 ?
                            " attributes: " + instance.getAttributes().keySet().stream()
                                    .map(s -> s + " = " + instance.getAttributes().get(s))
                                    .collect(Collectors.joining(", "))
                            : "")
                    ));

                    logger.info("");
                }
            }

        } catch (UnknownHostException e) {
            logger.error("Unknown host: ", e);
        } catch (IOException e) {
            logger.error("IO error: ", e);
        }
    }
}
