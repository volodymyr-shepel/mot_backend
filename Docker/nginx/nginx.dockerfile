FROM nginx:1.25.3

RUN apt-get update \
    && apt-get install -y wget gpg lsb-release \
    && rm -rf /var/lib/apt/lists/*

# Install Consul
RUN wget -O- https://apt.releases.hashicorp.com/gpg | gpg --dearmor -o /usr/share/keyrings/hashicorp-archive-keyring.gpg \
    && echo "deb [signed-by=/usr/share/keyrings/hashicorp-archive-keyring.gpg] https://apt.releases.hashicorp.com $(lsb_release -cs) main" | tee /etc/apt/sources.list.d/hashicorp.list \
    && apt-get update \
    && apt-get install -y consul \
    && rm -rf /var/lib/apt/lists/*

# Install Consul Template
RUN apt-get update \
    && apt-get install -y consul-template \
    && rm -rf /var/lib/apt/lists/*

# Remove the default Nginx configuration
RUN rm /etc/nginx/conf.d/default.conf

# Create the /etc/consul-template directory
RUN mkdir -p /etc/consul-template

# Copy Consul Template configuration file
COPY ./Docker/nginx/consul-template-config.hcl /etc/consul-template/consul-template-config.hcl

# Copy load balancer template file
COPY ./Docker/nginx/load-balancer.conf.ctmpl /etc/nginx/conf.d/load-balancer.conf.ctmpl

COPY ./Docker/nginx/entrypoint.sh /usr/local/bin/entrypoint.sh
RUN chmod +x /usr/local/bin/entrypoint.sh

ENTRYPOINT ["entrypoint.sh"]
CMD ["nginx", "-g", "daemon off;"]
## Start Consul and Nginx using consul-template
## CMD ["consul-template", "-config=/etc/consul-template/consul-template-config.hcl"]
#ENTRYPOINT ["consul-template", "-config=/etc/consul-template/consul-template-config.hcl"]