resource "keycloak_realm" "hive-admin" {
  realm                          = "Hive"
  display_name                   = "Hive"
  enabled                        = true
  registration_email_as_username = false
  login_with_email_allowed       = false
  duplicate_emails_allowed       = true
  reset_password_allowed         = false
  edit_username_allowed          = false
  registration_allowed           = false
  verify_email                   = false
  remember_me                    = true
  #  login_theme                    = "hive-admin"
}

resource "keycloak_openid_client" "hive-admin-front" {
  name                  = "Hive Admin frontend application"
  access_type           = "PUBLIC"
  client_id             = "hive-web-apps"
  realm_id              = keycloak_realm.hive-admin.id
  client_secret         = "top-secret-front-client-secret"
  standard_flow_enabled = true
  valid_redirect_uris   = [
    "*",
  ]
  web_origins = [
    "*",
  ]
}

resource "keycloak_openid_user_property_protocol_mapper" "username-sub-mapper" {
  claim_name    = "sub"
  name          = "username-sub-mapper"
  realm_id      = keycloak_realm.hive-admin.id
  user_property = "username"
  client_id     = keycloak_openid_client.hive-admin-front.id
}

resource "keycloak_openid_client" "hive-admin" {
  name                     = "Hive Admin backend application"
  access_type              = "CONFIDENTIAL"
  client_id                = "hive-admin"
  realm_id                 = keycloak_realm.hive-admin.id
  client_secret            = "top-secret-client-secret"
  service_accounts_enabled = true
  authorization {
    policy_enforcement_mode = "ENFORCING"
  }
}

data "keycloak_openid_client" "realm_management_client" {
  realm_id  = keycloak_realm.hive-admin.id
  client_id = "realm-management"
}

resource "keycloak_openid_client_service_account_role" "hive-admin-service-view-users" {
  realm_id                = keycloak_realm.hive-admin.id
  role                    = "view-users"
  service_account_user_id = keycloak_openid_client.hive-admin.service_account_user_id
  client_id               = data.keycloak_openid_client.realm_management_client.id
}

resource "keycloak_openid_client_service_account_role" "hive-admin-service-manage-users" {
  realm_id                = keycloak_realm.hive-admin.id
  role                    = "manage-users"
  service_account_user_id = keycloak_openid_client.hive-admin.service_account_user_id
  client_id               = data.keycloak_openid_client.realm_management_client.id
}

resource "keycloak_openid_client_service_account_role" "hive-admin-service-query-users" {
  realm_id                = keycloak_realm.hive-admin.id
  role                    = "query-users"
  service_account_user_id = keycloak_openid_client.hive-admin.service_account_user_id
  client_id               = data.keycloak_openid_client.realm_management_client.id
}

resource "keycloak_openid_client_service_account_role" "hive-admin-service-realm-admin" {
  realm_id                = keycloak_realm.hive-admin.id
  role                    = "realm-admin"
  service_account_user_id = keycloak_openid_client.hive-admin.service_account_user_id
  client_id               = data.keycloak_openid_client.realm_management_client.id
}

resource "keycloak_openid_client_service_account_role" "hive-admin-service-query-client" {
  realm_id                = keycloak_realm.hive-admin.id
  role                    = "query-clients"
  service_account_user_id = keycloak_openid_client.hive-admin.service_account_user_id
  client_id               = data.keycloak_openid_client.realm_management_client.id
}

resource "keycloak_openid_client_service_account_role" "hive-admin-service-query-realms" {
  realm_id                = keycloak_realm.hive-admin.id
  role                    = "query-realms"
  service_account_user_id = keycloak_openid_client.hive-admin.service_account_user_id
  client_id               = data.keycloak_openid_client.realm_management_client.id
}

# TODO remove this when Shopfloor and Hive Admin are merged
resource "keycloak_user" "shopfloor-manager-user" {
  realm_id = keycloak_realm.hive-admin.id
  username = "manager"
  initial_password {
    value     = "123456"
    temporary = false
  }
}
resource "keycloak_user" "shopfloor-operator-user" {
  realm_id = keycloak_realm.hive-admin.id
  username = "operator"
  initial_password {
    value     = "123456"
    temporary = false
  }
}
resource "keycloak_role" "shopfloor-manager-role" {
  realm_id = keycloak_realm.hive-admin.id
  name     = "manager"
}
resource "keycloak_role" "shopfloor-operator-role" {
  realm_id = keycloak_realm.hive-admin.id
  name     = "operator"
}
resource "keycloak_user_roles" "shopfloor-manager-user-roles" {
  realm_id = keycloak_realm.hive-admin.id
  role_ids = [keycloak_role.shopfloor-manager-role.id]
  user_id  = keycloak_user.shopfloor-manager-user.id
}
resource "keycloak_user_roles" "shopfloor-operator-user-roles" {
  realm_id = keycloak_realm.hive-admin.id
  role_ids = [keycloak_role.shopfloor-operator-role.id]
  user_id  = keycloak_user.shopfloor-operator-user.id
}
