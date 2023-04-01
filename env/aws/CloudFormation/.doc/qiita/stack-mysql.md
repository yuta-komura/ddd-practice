---
title: CloudFormationを使ってMySQLのRDSを構築する
tags: AWS CloudFormation RDS
author: okubot55
slide: false
---
#はじめに
本記事では、AWS CloudFormation管理コンソールを使って、RDS for MySQLを構築する手順を説明しています。（初学者向け）

本記事で掲載しているテンプレートの最新版は、下記に置いてます。
https://github.com/okubo-t/aws-cloudformation
#構成図
<img src="https://qiita-image-store.s3.ap-northeast-1.amazonaws.com/0/283246/30ae410c-a346-42da-b309-0c2379d58c63.png"　 width=70%>

#構築するコンポーネント
DBインスタンス
データベース
パラメータグループ
サブネットグループ
セキュリティグループ

#前提条件
下記の記事の構築手順で、VPCを構築していること。
[CloudFormationを使ってVPCを構築する]
(https://qiita.com/okubot55/items/b18a5dd5166f1ec2696c)

PJPrefixの値は、同一にすること。

#構築手順
１ AWS CloudFormation管理コンソールから、スタックの作成をクリックします。
<img src="https://qiita-image-store.s3.amazonaws.com/0/283246/e0c3e7c3-f478-18c7-2dc2-7e669d1b399d.png"　 width=50%>

２ 後述のテンプレートを選択します。
<img src="https://qiita-image-store.s3.amazonaws.com/0/283246/060a4709-622a-2394-de5f-ac2103045137.png"　 width=50%>

３ 各パラメータを入力します。
<img src="https://qiita-image-store.s3.amazonaws.com/0/283246/1a060895-30f3-d649-e9ce-cffff5fc6d8d.png"　 width=70%>

|パラメータ名|用途|備考|
|:---|:---|:---|
|スタックの名前|テンプレートから作成するリソース一式の名前|例　prd-stack-vpc-20180801|
|PJPrefix|構築するプロジェクトの環境を識別するために各コンポーネントの先頭に付与する識別子|例 qiita-prd|
|DBInstanceName|DBインスタンス名|rds(デフォルト)|
|MySQLMajorVersion|RDSのMySQLのメジャーバージョン|5.7(デフォルト)|
|MySQLMinorVersion|RDSのMySQLのマイナーバージョン|22(デフォルト)|
|DBInstanceClass|インスタンスクラス|db.m4.large(デフォルト)|
|DBInstanceStorageSize|ストレージサイズ|30GB(デフォルト)|
|DBInstanceStorageType|ストレージタイプ|gp2（デフォルト）|
|DBName|データベース名|30(デフォルト)|
|DBMasterUserName|マスターユーザー名|dbuser(デフォルト)|
|DBPassword|マスターユーザーのパスワード|dbpassword(デフォルト)|
|MultiAZ|MultiAZを有効にする場合は、true<br>MultiAZを有効にする場合は、false|false(デフォルト)|

4 後続は、デフォルトのまま次へ次へで、作成します。

５ 状況が CREATE COMPLETEになれば、RDS for MySQLの構築が完了です。
<img src="https://qiita-image-store.s3.amazonaws.com/0/283246/f919b748-e62b-9f47-0eed-a3ab499405f0.png"　 width=50%>

6 管理コンソールの下部の出力から、構築したRDSの情報を確認できます。
<img src="https://qiita-image-store.s3.amazonaws.com/0/283246/913f0712-09de-db6c-50b2-468158ae5de8.png"　 width=70%>

#テンプレート
```rds-mysql-01.yml
AWSTemplateFormatVersion: "2010-09-09"
Description:
  RDS for  MySQL Create

Metadata:
  "AWS::CloudFormation::Interface":
    ParameterGroups:
      - Label:
          default: "Project Name Prefix"
        Parameters:
          - PJPrefix
      - Label:
          default: "RDS Configuration"
        Parameters:
          - DBInstanceName
          - MySQLMajorVersion
          - MySQLMinorVersion
          - DBInstanceClass
          - DBInstanceStorageSize
          - DBInstanceStorageType
          - DBName
          - DBMasterUserName
          - DBPassword
          - MultiAZ
    ParameterLabels:
      DBInstanceName:
        default: "DBInstanceName"
      MySQLMajorVersion:
        default: "MySQLMajorVersion"
      MySQLMinorVersion:
        default: "MySQLMinorVersion"
      DBInstanceClass:
        default: "DBInstanceClass"
      DBInstanceStorageSize:
        default: "DBInstanceStorageSize"
      DBInstanceStorageType:
        default: "DBInstanceStorageType"
      DBName:
        default: "DBName"
      DBMasterUserName:
        default: "DBUserName"
      DBPassword:
        default: "DBPassword"
      MultiAZ:
        default: "MultiAZ"

# ------------------------------------------------------------#
# Input Parameters
# ------------------------------------------------------------# 
Parameters:
  PJPrefix:
    Type: String

  DBInstanceName:
    Type: String
    Default: "rds"
  MySQLMajorVersion:
    Type: String
    Default: "5.7"
    AllowedValues: [ "5.5", "5.6", "5.7" ]
  MySQLMinorVersion:
    Type: String
    Default: "22"
  DBInstanceClass:
    Type: String
    Default: "db.m4.large" 
  DBInstanceStorageSize:
    Type: String
    Default: "30"
  DBInstanceStorageType:
    Type: String
    Default: "gp2"
  DBName:
    Type: String
    Default: "db"
  DBMasterUserName:
    Type: String
    Default: "dbuser"
    NoEcho: true
    MinLength: 1
    MaxLength: 16
    AllowedPattern: "[a-zA-Z][a-zA-Z0-9]*"
    ConstraintDescription: "must begin with a letter and contain only alphanumeric characters."
  DBPassword: 
    Default: "dbpassword"
    NoEcho: true
    Type: String
    MinLength: 8
    MaxLength: 41
    AllowedPattern: "[a-zA-Z0-9]*"
    ConstraintDescription: "must contain only alphanumeric characters."
  MultiAZ: 
    Default: "false"
    Type: String
    AllowedValues: [ "true", "false" ]

Resources: 
# ------------------------------------------------------------#
#  DBInstance MySQL
# ------------------------------------------------------------#
  DBInstance: 
    Type: "AWS::RDS::DBInstance"
    Properties: 
      DBInstanceIdentifier: !Sub "${PJPrefix}-${DBInstanceName}"
      Engine: MySQL
      EngineVersion: !Sub "${MySQLMajorVersion}.${MySQLMinorVersion}"
      DBInstanceClass: !Ref DBInstanceClass
      AllocatedStorage: !Ref DBInstanceStorageSize
      StorageType: !Ref DBInstanceStorageType
      DBName: !Ref DBName
      MasterUsername: !Ref DBMasterUserName
      MasterUserPassword: !Ref DBPassword
      DBSubnetGroupName: !Ref DBSubnetGroup
      PubliclyAccessible: false
      MultiAZ: !Ref MultiAZ
      PreferredBackupWindow: "18:00-18:30"
      PreferredMaintenanceWindow: "sat:19:00-sat:19:30"
      AutoMinorVersionUpgrade: false
      DBParameterGroupName: !Ref DBParameterGroup  
      VPCSecurityGroups:
        - !Ref RDSSecurityGroup
      CopyTagsToSnapshot: true
      BackupRetentionPeriod: 7
      Tags: 
        - Key: "Name"
          Value: !Ref DBInstanceName
    DeletionPolicy: "Delete"

# ------------------------------------------------------------#
#  DBParameterGroup
# ------------------------------------------------------------#
  DBParameterGroup:
    Type: "AWS::RDS::DBParameterGroup"
    Properties:
      Family: !Sub "MySQL${MySQLMajorVersion}"
      Description: !Sub "${PJPrefix}-${DBInstanceName}-parm"

# ------------------------------------------------------------#
#  SecurityGroup for RDS (MySQL)
# ------------------------------------------------------------#
  RDSSecurityGroup:
    Type: "AWS::EC2::SecurityGroup"
    Properties:
      VpcId: { "Fn::ImportValue": !Sub "${PJPrefix}-vpc" }
      GroupName: !Sub "${PJPrefix}-${DBInstanceName}-sg"
      GroupDescription: "-"
      Tags:
        - Key: "Name"
          Value: !Sub "${PJPrefix}-${DBInstanceName}-sg"
# Rule
      SecurityGroupIngress:
        - IpProtocol: tcp
          FromPort: 3306
          ToPort: 3306
          CidrIp: { "Fn::ImportValue": !Sub "${PJPrefix}-vpc-cidr" }

# ------------------------------------------------------------#
#  DBSubnetGroup
# ------------------------------------------------------------#
  DBSubnetGroup: 
    Type: "AWS::RDS::DBSubnetGroup"
    Properties: 
      DBSubnetGroupName: !Sub "${PJPrefix}-${DBInstanceName}-subnet"
      DBSubnetGroupDescription: "-"
      SubnetIds: 
        - { "Fn::ImportValue": !Sub "${PJPrefix}-private-subnet-a" }
        - { "Fn::ImportValue": !Sub "${PJPrefix}-private-subnet-c" }

# ------------------------------------------------------------#
# Output Parameters
# ------------------------------------------------------------#                
Outputs:
#DBInstance
  DBInstanceID:
    Value: !Ref DBInstance
    Export:
      Name: !Sub "${PJPrefix}-${DBInstanceName}-id"

  DBInstanceEndpoint:
    Value: !GetAtt DBInstance.Endpoint.Address
    Export:
      Name: !Sub "${PJPrefix}-${DBInstanceName}-endpoint"

  DBName:
    Value: !Ref DBName
    Export:
      Name: !Sub "${PJPrefix}-${DBInstanceName}-dbname"
```

