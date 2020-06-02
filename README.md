mDns Exporer
====

mDns explorer is made for browse all local interface and get full info about devices, their addresses and attributes.

Based on https://github.com/fflewddur/hola 

# Features

- Scan all local interfaces;
- Discover all udp/tcp services;
- Output ip4/ipv6 + hostname address, service attributes
- Just start and browse, zero conf! :)

# Requirements

Hola requires Java 8 or higher, Maven to build.

# Run

mvn clean compile package
java -jar target/mdns_explorer-0.0.1-SNAPSHOT.jar 

# License

Hola is free software and released under the MIT License.