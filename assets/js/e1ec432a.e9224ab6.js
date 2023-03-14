"use strict";(self.webpackChunkwebsite=self.webpackChunkwebsite||[]).push([[336],{3905:(e,t,n)=>{n.d(t,{Zo:()=>u,kt:()=>m});var r=n(7294);function a(e,t,n){return t in e?Object.defineProperty(e,t,{value:n,enumerable:!0,configurable:!0,writable:!0}):e[t]=n,e}function o(e,t){var n=Object.keys(e);if(Object.getOwnPropertySymbols){var r=Object.getOwnPropertySymbols(e);t&&(r=r.filter((function(t){return Object.getOwnPropertyDescriptor(e,t).enumerable}))),n.push.apply(n,r)}return n}function i(e){for(var t=1;t<arguments.length;t++){var n=null!=arguments[t]?arguments[t]:{};t%2?o(Object(n),!0).forEach((function(t){a(e,t,n[t])})):Object.getOwnPropertyDescriptors?Object.defineProperties(e,Object.getOwnPropertyDescriptors(n)):o(Object(n)).forEach((function(t){Object.defineProperty(e,t,Object.getOwnPropertyDescriptor(n,t))}))}return e}function l(e,t){if(null==e)return{};var n,r,a=function(e,t){if(null==e)return{};var n,r,a={},o=Object.keys(e);for(r=0;r<o.length;r++)n=o[r],t.indexOf(n)>=0||(a[n]=e[n]);return a}(e,t);if(Object.getOwnPropertySymbols){var o=Object.getOwnPropertySymbols(e);for(r=0;r<o.length;r++)n=o[r],t.indexOf(n)>=0||Object.prototype.propertyIsEnumerable.call(e,n)&&(a[n]=e[n])}return a}var s=r.createContext({}),p=function(e){var t=r.useContext(s),n=t;return e&&(n="function"==typeof e?e(t):i(i({},t),e)),n},u=function(e){var t=p(e.components);return r.createElement(s.Provider,{value:t},e.children)},k="mdxType",c={inlineCode:"code",wrapper:function(e){var t=e.children;return r.createElement(r.Fragment,{},t)}},d=r.forwardRef((function(e,t){var n=e.components,a=e.mdxType,o=e.originalType,s=e.parentName,u=l(e,["components","mdxType","originalType","parentName"]),k=p(n),d=a,m=k["".concat(s,".").concat(d)]||k[d]||c[d]||o;return n?r.createElement(m,i(i({ref:t},u),{},{components:n})):r.createElement(m,i({ref:t},u))}));function m(e,t){var n=arguments,a=t&&t.mdxType;if("string"==typeof e||a){var o=n.length,i=new Array(o);i[0]=d;var l={};for(var s in t)hasOwnProperty.call(t,s)&&(l[s]=t[s]);l.originalType=e,l[k]="string"==typeof e?e:a,i[1]=l;for(var p=2;p<o;p++)i[p]=n[p];return r.createElement.apply(null,i)}return r.createElement.apply(null,n)}d.displayName="MDXCreateElement"},4755:(e,t,n)=>{n.r(t),n.d(t,{assets:()=>k,contentTitle:()=>p,default:()=>h,frontMatter:()=>s,metadata:()=>u,toc:()=>c});var r=n(7462),a=(n(7294),n(3905));const o={toc:[{value:"Overview",id:"overview",level:2},{value:"Installation",id:"installation",level:2},{value:"Prerequisites",id:"prerequisites",level:3},{value:"Installing",id:"installing",level:3},{value:"Running",id:"running",level:3},{value:"Configuration Properties",id:"configuration-properties",level:3},{value:"License",id:"license",level:2}]},i="wrapper";function l(e){let{components:t,...n}=e;return(0,a.kt)(i,(0,r.Z)({},o,n,{components:t,mdxType:"MDXLayout"}),(0,a.kt)("h1",{id:"skyhook"},"Skyhook"),(0,a.kt)("p",null,(0,a.kt)("a",{parentName:"p",href:"https://github.com/aerospike/skyhook/actions/workflows/build.yml"},(0,a.kt)("img",{parentName:"a",src:"https://github.com/aerospike/skyhook/actions/workflows/build.yml/badge.svg",alt:"Build"}))),(0,a.kt)("p",null,"Skyhook is a Redis API-compatible gateway to the ",(0,a.kt)("a",{parentName:"p",href:"https://www.aerospike.com/"},"Aerospike")," Database. Use Skyhook to quickly get your Redis client applications up and running on an Aerospike cluster."),(0,a.kt)("h2",{id:"overview"},"Overview"),(0,a.kt)("p",null,"Skyhook is designed as a standalone server application written in Kotlin, which\naccepts Redis protocol commands and projects them to an Aerospike cluster using\nthe Aerospike Java client under the hood. It uses ",(0,a.kt)("a",{parentName:"p",href:"https://netty.io/"},"Netty")," as\na non-blocking I/O client-server framework."),(0,a.kt)("p",null,"This project is now in ",(0,a.kt)("strong",{parentName:"p"},"beta"),". If you're an enterprise customer feel free to\nreach out to our support with feedback and feature requests.\nWe appreciate feedback from the Aerospike community on\n",(0,a.kt)("a",{parentName:"p",href:"https://github.com/aerospike/skyhook/issues"},"issues"),"\nrelated to Skyhook."),(0,a.kt)("h2",{id:"installation"},"Installation"),(0,a.kt)("h3",{id:"prerequisites"},"Prerequisites"),(0,a.kt)("ul",null,(0,a.kt)("li",{parentName:"ul"},"Java 8 or later"),(0,a.kt)("li",{parentName:"ul"},"Aerospike Server version 4.9+")),(0,a.kt)("h3",{id:"installing"},"Installing"),(0,a.kt)("p",null,"Skyhook is distributed as a jar file which may be downloaded from ",(0,a.kt)("a",{parentName:"p",href:"https://github.com/aerospike/skyhook/releases/latest"},"https://github.com/aerospike/skyhook/releases/latest"),"."),(0,a.kt)("h3",{id:"running"},"Running"),(0,a.kt)("p",null,"Usage:"),(0,a.kt)("pre",null,(0,a.kt)("code",{parentName:"pre",className:"language-text"},"% java -jar skyhook-[version]-all.jar -h\n\nUsage: skyhook [-h] [-f=<configFile>]\nRedis to Aerospike proxy server\n  -f, --config-file=<configFile>\n               yaml formatted configuration file\n  -h, --help   display this help and exit\n")),(0,a.kt)("p",null,"To run the server:"),(0,a.kt)("pre",null,(0,a.kt)("code",{parentName:"pre",className:"language-sh"},"java -jar skyhook-[version]-all.jar -f config/server.yml\n")),(0,a.kt)("p",null,"The configuration file carries all the settings the server needs and is in YAML\nformat. An example configuration file can be found in the ",(0,a.kt)("a",{parentName:"p",href:"https://github.com/aerospike/skyhook/blob/a0199da72222984c8417ccaa6e4a02064ed7224b/config/server.yml"},(0,a.kt)("inlineCode",{parentName:"a"},"config"))," folder of this repository.\nIf no configuration file is specified, the default settings will be applied."),(0,a.kt)("pre",null,(0,a.kt)("code",{parentName:"pre",className:"language-text"},"[main] INFO  c.a.skyhook.SkyhookServer$Companion - Starting the Server...\n")),(0,a.kt)("p",null,"Now the server is listening to the ",(0,a.kt)("inlineCode",{parentName:"p"},"config.redisPort")," (default: 6379) and is ready to serve."),(0,a.kt)("p",null,"If you wish to deploy Skyhook as a cluster of nodes, you can find some example configurations ",(0,a.kt)("a",{parentName:"p",href:"https://aerospike.github.io/skyhook/scaling-out"},"here"),"."),(0,a.kt)("h3",{id:"configuration-properties"},"Configuration Properties"),(0,a.kt)("p",null,"The default behavior may be customized by setting the following properties in the configuration file:"),(0,a.kt)("table",null,(0,a.kt)("thead",{parentName:"table"},(0,a.kt)("tr",{parentName:"thead"},(0,a.kt)("th",{parentName:"tr",align:null},"Property name"),(0,a.kt)("th",{parentName:"tr",align:null},"Description"),(0,a.kt)("th",{parentName:"tr",align:null},"Default value"))),(0,a.kt)("tbody",{parentName:"table"},(0,a.kt)("tr",{parentName:"tbody"},(0,a.kt)("td",{parentName:"tr",align:null},"hostList"),(0,a.kt)("td",{parentName:"tr",align:null},"The host list to seed the Aerospike cluster."),(0,a.kt)("td",{parentName:"tr",align:null},"localhost:3000")),(0,a.kt)("tr",{parentName:"tbody"},(0,a.kt)("td",{parentName:"tr",align:null},"namespace"),(0,a.kt)("td",{parentName:"tr",align:null},"The Aerospike namespace."),(0,a.kt)("td",{parentName:"tr",align:null},"test")),(0,a.kt)("tr",{parentName:"tbody"},(0,a.kt)("td",{parentName:"tr",align:null},"set"),(0,a.kt)("td",{parentName:"tr",align:null},"The Aerospike set name."),(0,a.kt)("td",{parentName:"tr",align:null},"redis")),(0,a.kt)("tr",{parentName:"tbody"},(0,a.kt)("td",{parentName:"tr",align:null},"clientPolicy"),(0,a.kt)("td",{parentName:"tr",align:null},"The Aerospike Java client ",(0,a.kt)("a",{parentName:"td",href:"https://docs.aerospike.com/apidocs/java/com/aerospike/client/policy/ClientPolicy.html"},"ClientPolicy")," configuration properties."),(0,a.kt)("td",{parentName:"tr",align:null},"ClientPolicyConfig")),(0,a.kt)("tr",{parentName:"tbody"},(0,a.kt)("td",{parentName:"tr",align:null},"bin"),(0,a.kt)("td",{parentName:"tr",align:null},"The Aerospike value bin name."),(0,a.kt)("td",{parentName:"tr",align:null},"b")),(0,a.kt)("tr",{parentName:"tbody"},(0,a.kt)("td",{parentName:"tr",align:null},"typeBin"),(0,a.kt)("td",{parentName:"tr",align:null},"The Aerospike value ",(0,a.kt)("a",{parentName:"td",href:"https://redis.io/topics/data-types"},"type")," bin name."),(0,a.kt)("td",{parentName:"tr",align:null},"t")),(0,a.kt)("tr",{parentName:"tbody"},(0,a.kt)("td",{parentName:"tr",align:null},"redisPort"),(0,a.kt)("td",{parentName:"tr",align:null},"The server port to bind to."),(0,a.kt)("td",{parentName:"tr",align:null},"6379")),(0,a.kt)("tr",{parentName:"tbody"},(0,a.kt)("td",{parentName:"tr",align:null},"workerThreads",(0,a.kt)("sup",null,(0,a.kt)("a",{parentName:"td",href:"#worker-threads"},"1"))),(0,a.kt)("td",{parentName:"tr",align:null},"The Netty worker group size."),(0,a.kt)("td",{parentName:"tr",align:null},"number of available cores")),(0,a.kt)("tr",{parentName:"tbody"},(0,a.kt)("td",{parentName:"tr",align:null},"bossThreads"),(0,a.kt)("td",{parentName:"tr",align:null},"The Netty acceptor group size."),(0,a.kt)("td",{parentName:"tr",align:null},"2")))),(0,a.kt)("sup",{name:"worker-threads"},"1")," Used to configure the size of the ",(0,a.kt)("a",{href:"https://www.aerospike.com/docs/client/java/usage/async/eventloop.html"},"Aerospike Java Client EventLoops")," as well.",(0,a.kt)("h2",{id:"license"},"License"),(0,a.kt)("p",null,"Licensed under an Apache 2.0 License."),(0,a.kt)("p",null,"This is an active open source project. You can contribute to it by trying\nSkyhook, providing feedback, reporting bugs, and implementing more Redis\ncommands."))}l.isMDXComponent=!0;const s={title:"",slug:"/"},p=void 0,u={unversionedId:"intro",id:"version-0.9.0/intro",title:"",description:"",source:"@site/versioned_docs/version-0.9.0/intro.mdx",sourceDirName:".",slug:"/",permalink:"/skyhook/0.9.0/",draft:!1,editUrl:"https://github.com/aerospike/skyhook/edit/main/website/versioned_docs/version-0.9.0/intro.mdx",tags:[],version:"0.9.0",frontMatter:{title:"",slug:"/"},sidebar:"version-0.9.0/docsSidebar",next:{title:"Supported Redis Commands",permalink:"/skyhook/0.9.0/supported-redis-api"}},k={},c=[],d={toc:c},m="wrapper";function h(e){let{components:t,...n}=e;return(0,a.kt)(m,(0,r.Z)({},d,n,{components:t,mdxType:"MDXLayout"}),(0,a.kt)(l,{mdxType:"Intro"}))}h.isMDXComponent=!0}}]);