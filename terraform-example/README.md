# Terraform Module Example

This example shows how to create and use a custom Terraform module for different environments.

## Structure
```
terraform-example/
├── modules/
│   └── web-server/
│       ├── main.tf
│       ├── variables.tf
│       └── outputs.tf
├── environments/
│   ├── dev/
│   │   └── main.tf
│   ├── staging/
│   │   └── main.tf
│   └── prod/
│       └── main.tf
└── README.md
```

## Usage

### Deploy Dev Environment
```bash
cd environments/dev
terraform init
terraform plan
terraform apply
```

### Deploy Staging Environment
```bash
cd environments/staging
terraform init
terraform plan
terraform apply
```

### Deploy Production Environment
```bash
cd environments/prod
terraform init
terraform plan
terraform apply
```

## What it creates
- EC2 instance with Apache web server
- Security group allowing HTTP (80) and SSH (22)
- Different instance sizes per environment:
  - Dev: t3.micro
  - Staging: t3.small
  - Prod: t3.medium

## Cleanup
```bash
# In each environment directory
terraform destroy
```
