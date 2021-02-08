#!/bin/sh
sed -i  -e s@mystok-gcp-dev-image-bucket@mystok-gcp-prod-image-bucket@g  src/main/webapp/*.jsp

