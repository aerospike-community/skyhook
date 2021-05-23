## Scaling out Skyhook
Skyhook is a stateless application, thus can be scaled with ease to handle higher loads.
There are several ways to split traffic across multiple nodes. Below is some quick documentation on how to scale Skyhook using well known open-source reverse proxy servers and enable SSL/TLS encryption along the way.

### Load balancing
Load balancing refers to efficiently distributing incoming network traffic across a group of backend servers. Modern high‑traffic services must serve a huge amount of concurrent requests and respond in a fast and reliable manner. To meet the scale, modern computing best practice generally requires adding more servers.
In this manner, a load balancer performs the following functions:
* Distributes client requests or network load efficiently across multiple servers
* Ensures high availability and reliability by sending requests only to servers that are online
* Provides the flexibility to add or subtract servers on demand

### Architecture Diagram
![](./images/scaling-out-diagram.png)

### Using Nginx as HTTP load balancer

#### Load balancing methods
The following load balancing mechanisms (or methods) are supported in nginx:
* round-robin — requests to the application servers are distributed in a round-robin fashion,
* least-connected — next request is assigned to the server with the least number of active connections,
* ip-hash — a hash-function is used to determine what server should be selected for the next request (based on the client’s IP address).

#### Default load balancing configuration
The simplest configuration for load balancing with nginx may look like the following:
```conf
http {
    upstream skyhook {
        server srv1.skyhook;
        server srv2.skyhook;
        server srv3.skyhook;
    }
    server {
        listen 80;

        location / {
            proxy_pass http://skyhook;
        }
    }
}
```

In the example above, there are 3 instances of Skyhook running on srv1-srv3. When the load balancing method is not specifically configured,
it defaults to round-robin. All requests are [proxied](http://nginx.org/en/docs/http/ngx_http_proxy_module.html#proxy_pass) to the server group skyhook, and nginx applies HTTP load balancing to distribute the requests.

You can read more about configuring the Nginx LB from their [official documentation](http://nginx.org/en/docs/http/load_balancing.html).

### Using HAProxy as HTTP load balancer
Look into [instructions](https://www.haproxy.com/documentation/hapee/latest/getting-started/installation/) on how to install HAProxy.

HAProxy's configuration file is /etc/haproxy/haproxy.cfg. This is where you make the changes to define your load balancer.
This [basic configuration](https://gist.github.com/haproxytechblog/38ef4b7d42f16cfe5c30f28ee3304dce) will get you started with a working server.

### SSL Termination
When you operate a farm of servers, it can be a tedious task maintaining SSL certificates.
Even using a Let’s Encrypt Certbot to automatically update certificates has its challenges because,
unless you have the ability to dynamically update DNS records as part of the certificate renewal process,
it may necessitate making your web servers directly accessible from the Internet so that Let’s Encrypt servers can verify that you own your domain.

Enabling SSL on your web servers also costs more CPU usage, since those servers must become involved in encrypting and decrypting messages.
That CPU time could otherwise have been used to do other meaningful work. Web servers can process requests more quickly if they’re not also crunching through encryption algorithms simultaneously.

The term SSL termination means that you are performing all encryption and decryption at the edge of your network, such as at the load balancer.
The load balancer strips away the encryption and passes the messages in the clear to your servers. You might also hear this called SSL offloading.

SSL termination has many benefits. These include the following:
* You can maintain certificates in fewer places, making your job easier.
* You don’t need to expose your servers to the Internet for certificate renewal purposes.
* Servers are unburdened from the task of processing encrypted messages, freeing up CPU time.

### Enabling SSL with Nginx
To configure an HTTPS server, the ssl parameter must be enabled on [listening sockets](http://nginx.org/en/docs/http/ngx_http_core_module.html#listen) in the [server](http://nginx.org/en/docs/http/ngx_http_core_module.html#server) block,
and the locations of the [server certificate](http://nginx.org/en/docs/http/ngx_http_ssl_module.html#ssl_certificate) and [private key](http://nginx.org/en/docs/http/ngx_http_ssl_module.html#ssl_certificate_key) files should be specified.

See the [official documentation](http://nginx.org/en/docs/http/configuring_https_servers.html) on how to configure HTTPS using Nginx.

### Enabling SSL with HAProxy
HAProxy Enterprise supports Transport Layer Security (TLS) for encrypting traffic between itself and clients. You have the option of using or not using TLS between HAProxy Enterprise and your backend servers, if you require end-to-end encryption.

Read more about [Enabling SSL with HAProxy](https://www.haproxy.com/blog/haproxy-ssl-termination/).
