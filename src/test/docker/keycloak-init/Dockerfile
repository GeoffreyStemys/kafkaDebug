FROM hashicorp/terraform:1.4.5

ENV KEYCLOAK_ADMIN_USER ${KEYCLOAK_ADMIN_USER}
ENV KEYCLOAK_ADMIN_PASSWORD ${KEYCLOAK_ADMIN_PASSWORD}
ENV KEYCLOAK_URL ${KEYCLOAK_URL}

VOLUME /terraform
WORKDIR /terraform

RUN apk update && apk upgrade && apk add curl && apk add dos2unix

COPY entrypoint.sh .
COPY provider.tf .
COPY realm.tf .

RUN echo "hello"

RUN dos2unix ./entrypoint.sh

ENTRYPOINT ["ash", "entrypoint.sh"]
