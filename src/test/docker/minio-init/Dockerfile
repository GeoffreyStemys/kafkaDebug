FROM hashicorp/terraform:1.4.5

RUN apk update && apk upgrade && apk add curl && apk add dos2unix

COPY entrypoint.sh .
COPY provider.tf .
COPY minio.tf .

RUN dos2unix ./entrypoint.sh

ENTRYPOINT ["ash", "entrypoint.sh"]
