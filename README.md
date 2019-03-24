Lab#2/Group3/2019
-------
[![Codacy Badge](https://api.codacy.com/project/badge/Grade/0c31ab3c3a01472884df2380a7832a09)](https://app.codacy.com/app/EEM86/WooChat?utm_source=github.com&utm_medium=referral&utm_content=EEM86/WooChat&utm_campaign=Badge_Grade_Dashboard)
[![Java 8+](https://img.shields.io/badge/java-8%2b-green.svg)](http://www.oracle.com/technetwork/java/javase/downloads/index.html)
[![Build Status](https://semaphoreci.com/api/v1/eem86/woochat/branches/master/badge.svg)](https://semaphoreci.com/eem86/woochat)
---
Instant Messenger
=====================
A server-client application for real-time messaging.
![screenshot of sample](https://i.imgur.com/HaF8Fgp.png)
![screenshot of sample](https://i.imgur.com/cQtbkRD.png)
---
**About WooChat**
1. Server app has console version. Client app - GUI. Apps have config files, where server IP and server PORT are saved.
2. Server listens to two kinds of ports (both are saved in server.properties config file). First port accepts connection. After that server moves this connection to another port for chatting.
3. After registration/authorization user appears in the main chat window - WooChat. After that he can create a private conversation. Also he can add another members to the conversation. Thus, a private group will be created. 
4. Root Admin's login is saved into server.properties config file. Admin can kick, ban and unban users.
5. Server saves history of private groups.
6. User inactivity period (1 hour as default) is saved into server.properties config file. Admin can change this parameter.
7. Admin can stop server, changes its configs: ports, timeout inactivity. He can type </help> in the chat and get further clues.
8. XML is used for communication between Server and Client.
