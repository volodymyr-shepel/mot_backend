#!/bin/bash

# Run consul-template in the background
consul-template -config=/etc/consul-template/consul-template-config.hcl &
# Start nginx
exec "$@"
