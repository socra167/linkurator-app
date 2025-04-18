terraform {
  required_providers {
    ncloud = {
      source  = "NaverCloudPlatform/ncloud"
      version = "3.3.1"
    }
  }
}

provider "ncloud" {
  access_key  = var.access_key
  secret_key  = var.secret_key
  region      = var.region
  site = "public"  # 또는 "private" 가능
  support_vpc = true
}

# VPC 생성
resource "ncloud_vpc" "vpc" {
  name            = "${var.prefix}-vpc"
  ipv4_cidr_block = "10.0.0.0/16"
}

# Network acl
resource "ncloud_network_acl" "nacl" {
  vpc_no = ncloud_vpc.vpc.id
  name   = "${var.prefix}-linkurator-nacl"
}

# Network acl rule
resource "ncloud_network_acl_rule" "nacl_rule" {
  network_acl_no = ncloud_network_acl.nacl.id

  inbound {
    priority    = 100
    protocol    = "TCP"
    rule_action = "ALLOW"
    ip_block    = "0.0.0.0/0"
    port_range  = "22"
  }

  inbound {
    priority    = 110
    protocol    = "TCP"
    rule_action = "ALLOW"
    ip_block    = "0.0.0.0/0"
    port_range  = "80"
  }

  outbound {
    priority    = 100
    protocol    = "TCP"
    rule_action = "ALLOW"
    ip_block    = "0.0.0.0/0"
    port_range  = "1-65535"
  }
}

# Subnet 생성
resource "ncloud_subnet" "subnet_01" {
  vpc_no         = ncloud_vpc.vpc.id
  subnet         = "10.0.1.0/24"
  zone           = "KR-2"
  network_acl_no = ncloud_network_acl.nacl.network_acl_no
  subnet_type    = "PUBLIC" // PUBLIC(Public) | PRIVATE(Private)
  // below fields is optional
  name           = "${var.prefix}-01"
  usage_type     = "GEN"    // GEN(General) | LOADB(For load balancer)
}

# network interface
resource "ncloud_network_interface" "nic" {
  name       = "${var.prefix}-nic"
  subnet_no  = ncloud_subnet.subnet_01.id
  private_ip = "10.0.1.6"
  access_control_groups = [ncloud_vpc.vpc.default_access_control_group_no]
}

# Route table
resource "ncloud_route_table" "public_route_table" {
  vpc_no = ncloud_vpc.vpc.id
  supported_subnet_type = "PUBLIC" // PUBLIC | PRIVATE
  name   = "${var.prefix}-public-rt"
}

resource "ncloud_route_table_association" "route_table_subnet" {
  route_table_no = ncloud_route_table.public_route_table.id
  subnet_no      = ncloud_subnet.subnet_01.id
}

# Import generated login key
import {
  to = ncloud_login_key.login_key
  id = var.login_key_name
}

resource "ncloud_login_key" "login_key" {
  key_name = "ncp-main-key"
}

# Init Script
resource "ncloud_init_script" "init" {
  name = "ls-script"
  content = file("${path.module}/init.sh")
}

# Server [ rocky-9.4, Micro vCPU 1EA, Memory 1GB ]
resource "ncloud_server" "app_server" {
  subnet_no            = ncloud_subnet.subnet_01.id
  name                 = "${var.prefix}-app-server"
  server_image_number  = "100524418"
  server_spec_code     = "mi1-g3"
  login_key_name       = ncloud_login_key.login_key.key_name
  fee_system_type_code = "FXSUM"
  init_script_no       = ncloud_init_script.init.id
}
