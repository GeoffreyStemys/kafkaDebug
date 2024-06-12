#!/bin/ash

echo "Waiting for $MINIO_URL ..."
ash -c "while ! curl -o - http://$MINIO_URL; do sleep 1; done;"

terraform init
terraform apply -auto-approve -var="username=$MINIO_ROOT_USER" -var="password=$MINIO_ROOT_PASSWORD" -var "host=$MINIO_URL"
