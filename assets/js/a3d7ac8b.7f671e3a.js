"use strict";(self.webpackChunkwebsite=self.webpackChunkwebsite||[]).push([[950],{7487:function(e,t,n){n.r(t),n.d(t,{frontMatter:function(){return s},contentTitle:function(){return l},metadata:function(){return c},toc:function(){return h},default:function(){return d}});var a=n(7462),i=n(3366),o=(n(7294),n(3905)),r=["components"],s={},l="Scaling Out",c={unversionedId:"scaling-out",id:"scaling-out",isDocsHomePage:!1,title:"Scaling Out",description:"Scaling out Skyhook",source:"@site/docs/scaling-out.md",sourceDirName:".",slug:"/scaling-out",permalink:"/skyhook/scaling-out",editUrl:"https://github.com/aerospike/skyhook/edit/main/website/docs/scaling-out.md",tags:[],version:"current",frontMatter:{},sidebar:"docsSidebar",previous:{title:"Supported Redis Commands",permalink:"/skyhook/supported-redis-api"},next:{title:"Usage With Redis client",permalink:"/skyhook/usage"}},h=[{value:"Scaling out Skyhook",id:"scaling-out-skyhook",children:[{value:"Load balancing",id:"load-balancing",children:[]},{value:"Architecture Diagram",id:"architecture-diagram",children:[]},{value:"Using Nginx as HTTP load balancer",id:"using-nginx-as-http-load-balancer",children:[]},{value:"Using HAProxy as HTTP load balancer",id:"using-haproxy-as-http-load-balancer",children:[]},{value:"SSL Termination",id:"ssl-termination",children:[]},{value:"Enabling SSL with Nginx",id:"enabling-ssl-with-nginx",children:[]},{value:"Enabling SSL with HAProxy",id:"enabling-ssl-with-haproxy",children:[]}]}],u={toc:h};function d(e){var t=e.components,s=(0,i.Z)(e,r);return(0,o.kt)("wrapper",(0,a.Z)({},u,s,{components:t,mdxType:"MDXLayout"}),(0,o.kt)("h1",{id:"scaling-out"},"Scaling Out"),(0,o.kt)("h2",{id:"scaling-out-skyhook"},"Scaling out Skyhook"),(0,o.kt)("p",null,"Skyhook is a stateless application, thus can be scaled with ease to handle higher loads.\nThere are several ways to split traffic across multiple nodes. Below is some quick documentation on how to scale Skyhook using well known open-source reverse proxy servers and enable SSL/TLS encryption along the way."),(0,o.kt)("h3",{id:"load-balancing"},"Load balancing"),(0,o.kt)("p",null,"Load balancing refers to efficiently distributing incoming network traffic across a group of backend servers. Modern high\u2011traffic services must serve a huge amount of concurrent requests and respond in a fast and reliable manner. To meet the scale, modern computing best practice generally requires adding more servers.\nIn this manner, a load balancer performs the following functions:"),(0,o.kt)("ul",null,(0,o.kt)("li",{parentName:"ul"},"Distributes client requests or network load efficiently across multiple servers"),(0,o.kt)("li",{parentName:"ul"},"Ensures high availability and reliability by sending requests only to servers that are online"),(0,o.kt)("li",{parentName:"ul"},"Provides the flexibility to add or subtract servers on demand")),(0,o.kt)("h3",{id:"architecture-diagram"},"Architecture Diagram"),(0,o.kt)("p",null,(0,o.kt)("img",{alt:"Scaling Out",src:n(329).Z})),(0,o.kt)("h3",{id:"using-nginx-as-http-load-balancer"},"Using Nginx as HTTP load balancer"),(0,o.kt)("h4",{id:"load-balancing-methods"},"Load balancing methods"),(0,o.kt)("p",null,"The following load balancing mechanisms (or methods) are supported in nginx:"),(0,o.kt)("ul",null,(0,o.kt)("li",{parentName:"ul"},"round-robin \u2014 requests to the application servers are distributed in a round-robin fashion,"),(0,o.kt)("li",{parentName:"ul"},"least-connected \u2014 next request is assigned to the server with the least number of active connections,"),(0,o.kt)("li",{parentName:"ul"},"ip-hash \u2014 a hash-function is used to determine what server should be selected for the next request (based on the client\u2019s IP address).")),(0,o.kt)("h4",{id:"default-load-balancing-configuration"},"Default load balancing configuration"),(0,o.kt)("p",null,"The simplest configuration for load balancing with nginx may look like the following:"),(0,o.kt)("pre",null,(0,o.kt)("code",{parentName:"pre",className:"language-conf"},"http {\n    upstream skyhook {\n        server srv1.skyhook;\n        server srv2.skyhook;\n        server srv3.skyhook;\n    }\n    server {\n        listen 80;\n\n        location / {\n            proxy_pass http://skyhook;\n        }\n    }\n}\n")),(0,o.kt)("p",null,"In the example above, there are 3 instances of Skyhook running on srv1-srv3. When the load balancing method is not specifically configured,\nit defaults to round-robin. All requests are ",(0,o.kt)("a",{parentName:"p",href:"http://nginx.org/en/docs/http/ngx_http_proxy_module.html#proxy_pass"},"proxied")," to the server group skyhook, and nginx applies HTTP load balancing to distribute the requests."),(0,o.kt)("p",null,"You can read more about configuring the Nginx LB from their ",(0,o.kt)("a",{parentName:"p",href:"http://nginx.org/en/docs/http/load_balancing.html"},"official documentation"),"."),(0,o.kt)("h3",{id:"using-haproxy-as-http-load-balancer"},"Using HAProxy as HTTP load balancer"),(0,o.kt)("p",null,"Look into ",(0,o.kt)("a",{parentName:"p",href:"https://www.haproxy.com/documentation/hapee/latest/getting-started/installation/"},"instructions")," on how to install HAProxy."),(0,o.kt)("p",null,"HAProxy's configuration file is /etc/haproxy/haproxy.cfg. This is where you make the changes to define your load balancer.\nThis ",(0,o.kt)("a",{parentName:"p",href:"https://gist.github.com/haproxytechblog/38ef4b7d42f16cfe5c30f28ee3304dce"},"basic configuration")," will get you started with a working server."),(0,o.kt)("h3",{id:"ssl-termination"},"SSL Termination"),(0,o.kt)("p",null,"When you operate a farm of servers, it can be a tedious task maintaining SSL certificates.\nEven using a Let\u2019s Encrypt Certbot to automatically update certificates has its challenges because,\nunless you have the ability to dynamically update DNS records as part of the certificate renewal process,\nit may necessitate making your web servers directly accessible from the Internet so that Let\u2019s Encrypt servers can verify that you own your domain."),(0,o.kt)("p",null,"Enabling SSL on your web servers also costs more CPU usage, since those servers must become involved in encrypting and decrypting messages.\nThat CPU time could otherwise have been used to do other meaningful work. Web servers can process requests more quickly if they\u2019re not also crunching through encryption algorithms simultaneously."),(0,o.kt)("p",null,"The term SSL termination means that you are performing all encryption and decryption at the edge of your network, such as at the load balancer.\nThe load balancer strips away the encryption and passes the messages in the clear to your servers. You might also hear this called SSL offloading."),(0,o.kt)("p",null,"SSL termination has many benefits. These include the following:"),(0,o.kt)("ul",null,(0,o.kt)("li",{parentName:"ul"},"You can maintain certificates in fewer places, making your job easier."),(0,o.kt)("li",{parentName:"ul"},"You don\u2019t need to expose your servers to the Internet for certificate renewal purposes."),(0,o.kt)("li",{parentName:"ul"},"Servers are unburdened from the task of processing encrypted messages, freeing up CPU time.")),(0,o.kt)("h3",{id:"enabling-ssl-with-nginx"},"Enabling SSL with Nginx"),(0,o.kt)("p",null,"To configure an HTTPS server, the ssl parameter must be enabled on ",(0,o.kt)("a",{parentName:"p",href:"http://nginx.org/en/docs/http/ngx_http_core_module.html#listen"},"listening sockets")," in the ",(0,o.kt)("a",{parentName:"p",href:"http://nginx.org/en/docs/http/ngx_http_core_module.html#server"},"server")," block,\nand the locations of the ",(0,o.kt)("a",{parentName:"p",href:"http://nginx.org/en/docs/http/ngx_http_ssl_module.html#ssl_certificate"},"server certificate")," and ",(0,o.kt)("a",{parentName:"p",href:"http://nginx.org/en/docs/http/ngx_http_ssl_module.html#ssl_certificate_key"},"private key")," files should be specified."),(0,o.kt)("p",null,"See the ",(0,o.kt)("a",{parentName:"p",href:"http://nginx.org/en/docs/http/configuring_https_servers.html"},"official documentation")," on how to configure HTTPS using Nginx."),(0,o.kt)("h3",{id:"enabling-ssl-with-haproxy"},"Enabling SSL with HAProxy"),(0,o.kt)("p",null,"HAProxy Enterprise supports Transport Layer Security (TLS) for encrypting traffic between itself and clients. You have the option of using or not using TLS between HAProxy Enterprise and your backend servers, if you require end-to-end encryption."),(0,o.kt)("p",null,"Read more about ",(0,o.kt)("a",{parentName:"p",href:"https://www.haproxy.com/blog/haproxy-ssl-termination/"},"Enabling SSL with HAProxy"),"."))}d.isMDXComponent=!0},329:function(e,t,n){t.Z=n.p+"assets/images/scaling-out-diagram-2bceef3acef8503f7ba91fc44db5d19f.png"}}]);