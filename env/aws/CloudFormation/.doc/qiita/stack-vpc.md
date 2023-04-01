---
title: CloudFormationを使ってVPCを構築する
tags: AWS CloudFormation vpc
author: okubot55
slide: false
---
#はじめに
本記事では、AWS CloudFormation管理コンソールを使って、VPCを構築する手順を説明しています。（初学者向け）

本記事で掲載しているテンプレートの最新版は、下記に置いてます。
https://github.com/okubo-t/aws-cloudformation
#構成図
<img src="https://qiita-image-store.s3.ap-northeast-1.amazonaws.com/0/283246/915dca28-e9ff-3070-de5a-ba6ace35afef.png" width=70%>

#構築するコンポーネント
[VPC(Virtual Private Cloud)とサブネット](https://docs.aws.amazon.com/ja_jp/vpc/latest/userguide/VPC_Subnets.html)
[インターネットゲートウェイ](https://docs.aws.amazon.com/ja_jp/vpc/latest/userguide/VPC_Internet_Gateway.html)
[ルートテーブル](https://docs.aws.amazon.com/ja_jp/vpc/latest/userguide/VPC_Route_Tables.html)

#構築手順
１ AWS CloudFormation管理コンソールから、スタックの作成をクリックし、[新しいリソースを使用(標準)]を選択します。
<img src="https://qiita-image-store.s3.ap-northeast-1.amazonaws.com/0/283246/4981a1e7-7a3c-13a7-cc05-c7ca3c9e999d.png"　 width=90%>

２ [スタックの作成]で、[テンプレートの準備完了]を選択し、[テンプレートファイルのアップロード]>
[ファイルの選択]で、後述のテンプレートを選択します。
<img src="https://qiita-image-store.s3.ap-northeast-1.amazonaws.com/0/283246/60b5e83c-6bda-4524-0cc9-a6a281527dfe.png"　 width=90%>

３ 各パラメータを入力します。
<img src="https://qiita-image-store.s3.ap-northeast-1.amazonaws.com/0/283246/8baf44ed-b127-b652-b947-388b201cb937.png"　 width=80%>

|パラメータ名|用途|備考|
|:---|:---|:---|
|スタックの名前|テンプレートから作成するリソース一式の名前|例　prd-stack-vpc-20180801|
|PJPrefix|構築するプロジェクトの環境を識別するために各コンポーネントの先頭に付与する識別子|例 qiita-prd|
|VPCCIDR|VPCのCDIR|10.1.0.0/16（デフォルト）|
|PublicSubnetA CIDR|構成図のPublic Subnet AのCIDR|10.1.10.0/24（デフォルト）|
|PublicSubnetC CIDR|構成図のPublic Subnet CのCIDR|10.1.20.0/24（デフォルト）|
|PrivateSubnetA CIDR|構成図のPrivate Subnet AのCIDR|10.1.100.0/24（デフォルト）|
|PrivateSubnetC CIDR|構成図のPrivate Subnet CのCIDR|10.1.200.0/24（デフォルト）|

4 後続は、デフォルトのまま次へ次へで、[スタックの作成]を押下して、作成します。
<img src="https://qiita-image-store.s3.ap-northeast-1.amazonaws.com/0/283246/6a28c35d-fd8d-d77d-ecb8-b518a6345826.png" width=50%>

5 [スタックの状態]を確認し CREATE_COMPLETEになれば、VPCの構築が完了です。
<img src="https://qiita-image-store.s3.ap-northeast-1.amazonaws.com/0/283246/0668e0b9-b4aa-e710-5535-07943331c453.png"　 width=50%>

#テンプレート
```vpc-01.yml
AWSTemplateFormatVersion: "2010-09-09"
Description: 
  VPC and Subnet Create

Metadata: 
  "AWS::CloudFormation::Interface": 
    ParameterGroups: 
      - Label: 
          default: "Project Name Prefix"
        Parameters: 
          - PJPrefix
      - Label: 
          default: "Network Configuration"
        Parameters: 
          - VPCCIDR
          - PublicSubnetACIDR
          - PublicSubnetCCIDR
          - PrivateSubnetACIDR
          - PrivateSubnetCCIDR
    ParameterLabels: 
      VPCCIDR: 
        default: "VPC CIDR"
      PublicSubnetACIDR: 
        default: "PublicSubnetA CIDR"
      PublicSubnetCCIDR: 
        default: "PublicSubnetC CIDR"
      PrivateSubnetACIDR: 
        default: "PrivateSubnetA CIDR"
      PrivateSubnetCCIDR: 
        default: "PrivateSubnetC CIDR"

# ------------------------------------------------------------#
# Input Parameters
# ------------------------------------------------------------# 
Parameters:
  PJPrefix:
    Type: String

  VPCCIDR:
    Type: String
    Default: "10.1.0.0/16"

  PublicSubnetACIDR:
    Type: String
    Default: "10.1.10.0/24"

  PublicSubnetCCIDR:
    Type: String
    Default: "10.1.20.0/24"

  PrivateSubnetACIDR:
    Type: String
    Default: "10.1.100.0/24"

  PrivateSubnetCCIDR:
    Type: String
    Default: "10.1.200.0/24"

Resources: 
# ------------------------------------------------------------#
#  VPC
# ------------------------------------------------------------#
# VPC Create
  VPC: 
    Type: "AWS::EC2::VPC"
    Properties: 
      CidrBlock: !Ref VPCCIDR
      EnableDnsSupport: "true"
      EnableDnsHostnames: "true"
      InstanceTenancy: default
      Tags: 
        - Key: Name
          Value: !Sub "${PJPrefix}-vpc"

# InternetGateway Create
  InternetGateway: 
    Type: "AWS::EC2::InternetGateway"
    Properties: 
      Tags: 
        - Key: Name
          Value: !Sub "${PJPrefix}-igw"

# IGW Attach
  InternetGatewayAttachment: 
    Type: "AWS::EC2::VPCGatewayAttachment"
    Properties: 
      InternetGatewayId: !Ref InternetGateway
      VpcId: !Ref VPC 

# ------------------------------------------------------------#
#  Subnet
# ------------------------------------------------------------#          
# Public SubnetA Create
  PublicSubnetA: 
    Type: "AWS::EC2::Subnet"
    Properties: 
      AvailabilityZone: "ap-northeast-1a"
      CidrBlock: !Ref PublicSubnetACIDR
      VpcId: !Ref VPC 
      Tags: 
        - Key: Name
          Value: !Sub "${PJPrefix}-public-subnet-a"

# Public SubnetC Create
  PublicSubnetC: 
    Type: "AWS::EC2::Subnet"
    Properties: 
      AvailabilityZone: "ap-northeast-1c"
      CidrBlock: !Ref PublicSubnetCCIDR
      VpcId: !Ref VPC 
      Tags: 
        - Key: Name
          Value: !Sub "${PJPrefix}-public-subnet-c"
                    
# Private SubnetA Create
  PrivateSubnetA: 
    Type: "AWS::EC2::Subnet"
    Properties: 
      AvailabilityZone: "ap-northeast-1a"
      CidrBlock: !Ref PrivateSubnetACIDR
      VpcId: !Ref VPC 
      Tags: 
        - Key: Name
          Value: !Sub "${PJPrefix}-private-subnet-a"

# Private SubnetC Create
  PrivateSubnetC: 
    Type: "AWS::EC2::Subnet"
    Properties: 
      AvailabilityZone: "ap-northeast-1c"
      CidrBlock: !Ref PrivateSubnetCCIDR
      VpcId: !Ref VPC 
      Tags: 
        - Key: Name
          Value: !Sub "${PJPrefix}-private-subnet-c"
                    
# ------------------------------------------------------------#
#  RouteTable
# ------------------------------------------------------------#          
# Public RouteTableA Create
  PublicRouteTableA: 
    Type: "AWS::EC2::RouteTable"
    Properties: 
      VpcId: !Ref VPC 
      Tags: 
        - Key: Name
          Value: !Sub "${PJPrefix}-public-route-a"
          
# Public RouteTableC Create
  PublicRouteTableC: 
    Type: "AWS::EC2::RouteTable"
    Properties: 
      VpcId: !Ref VPC 
      Tags: 
        - Key: Name
          Value: !Sub "${PJPrefix}-public-route-c"

# Private RouteTableA Create
  PrivateRouteTableA: 
    Type: "AWS::EC2::RouteTable"
    Properties: 
      VpcId: !Ref VPC 
      Tags: 
        - Key: Name
          Value: !Sub "${PJPrefix}-private-route-a"

# Private RouteTableC Create
  PrivateRouteTableC: 
    Type: "AWS::EC2::RouteTable"
    Properties: 
      VpcId: !Ref VPC 
      Tags: 
        - Key: Name
          Value: !Sub "${PJPrefix}-private-route-c"

# ------------------------------------------------------------#
# Routing
# ------------------------------------------------------------# 
# PublicRouteA Create
  PublicRouteA: 
    Type: "AWS::EC2::Route"
    Properties: 
      RouteTableId: !Ref PublicRouteTableA 
      DestinationCidrBlock: "0.0.0.0/0"
      GatewayId: !Ref InternetGateway 

# PublicRouteC Create
  PublicRouteC: 
    Type: "AWS::EC2::Route"
    Properties: 
      RouteTableId: !Ref PublicRouteTableC 
      DestinationCidrBlock: "0.0.0.0/0"
      GatewayId: !Ref InternetGateway 

# ------------------------------------------------------------#
# RouteTable Associate
# ------------------------------------------------------------# 
# PublicRouteTable Associate SubnetA
  PublicSubnetARouteTableAssociation: 
    Type: "AWS::EC2::SubnetRouteTableAssociation"
    Properties: 
      SubnetId: !Ref PublicSubnetA 
      RouteTableId: !Ref PublicRouteTableA

# PublicRouteTable Associate SubnetC
  PublicSubnetCRouteTableAssociation: 
    Type: "AWS::EC2::SubnetRouteTableAssociation"
    Properties: 
      SubnetId: !Ref PublicSubnetC 
      RouteTableId: !Ref PublicRouteTableC
                
# PrivateRouteTable Associate SubnetA
  PrivateSubnetARouteTableAssociation: 
    Type: "AWS::EC2::SubnetRouteTableAssociation"
    Properties: 
      SubnetId: !Ref PrivateSubnetA
      RouteTableId: !Ref PrivateRouteTableA 

# PrivateRouteTable Associate SubnetC
  PrivateSubnetCRouteTableAssociation: 
    Type: "AWS::EC2::SubnetRouteTableAssociation"
    Properties: 
      SubnetId: !Ref PrivateSubnetC
      RouteTableId: !Ref PrivateRouteTableC

# ------------------------------------------------------------#
# Output Parameters
# ------------------------------------------------------------#                
Outputs:
# VPC
  VPC:
    Value: !Ref VPC
    Export:
      Name: !Sub "${PJPrefix}-vpc"

  VPCCIDR:
    Value: !Ref VPCCIDR
    Export:
      Name: !Sub "${PJPrefix}-vpc-cidr"

# Subnet
  PublicSubnetA:
    Value: !Ref PublicSubnetA
    Export:
      Name: !Sub "${PJPrefix}-public-subnet-a"

  PublicSubnetACIDR:
    Value: !Ref PublicSubnetACIDR
    Export:
      Name: !Sub "${PJPrefix}-public-subnet-a-cidr"

  PublicSubnetC:
    Value: !Ref PublicSubnetC
    Export:
      Name: !Sub "${PJPrefix}-public-subnet-c"

  PublicSubnetCCIDR:
    Value: !Ref PublicSubnetCCIDR
    Export:
      Name: !Sub "${PJPrefix}-public-subnet-c-cidr"

  PrivateSubnetA:
    Value: !Ref PrivateSubnetA
    Export:
      Name: !Sub "${PJPrefix}-private-subnet-a"
    
  PrivateSubnetACIDR:
    Value: !Ref PrivateSubnetACIDR
    Export:
      Name: !Sub "${PJPrefix}-private-subnet-a-cidr"

  PrivateSubnetC:
    Value: !Ref PrivateSubnetC
    Export:
      Name: !Sub "${PJPrefix}-private-subnet-c"

  PrivateSubnetCCIDR:
    Value: !Ref PrivateSubnetCCIDR
    Export:
      Name: !Sub "${PJPrefix}-private-subnet-c-cidr"

# Route
  PublicRouteTableA:
    Value: !Ref PublicRouteTableA
    Export:
      Name: !Sub "${PJPrefix}-public-route-a"

  PublicRouteTableC:
    Value: !Ref PublicRouteTableC
    Export:
      Name: !Sub "${PJPrefix}-public-route-c"

  PrivateRouteTableA:
    Value: !Ref PrivateRouteTableA
    Export:
      Name: !Sub "${PJPrefix}-private-route-a"

  PrivateRouteTableC:
    Value: !Ref PrivateRouteTableC
    Export:
      Name: !Sub "${PJPrefix}-private-route-c"
```

