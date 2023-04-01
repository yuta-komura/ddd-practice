---
title: CloudFormationを使って最新のAmazonLinuxのAMIでEC2を構築する
tags: AWS CloudFormation EC2
author: okubot55
slide: false
---
#はじめに
本記事では、AWS CloudFormation管理コンソールを使って、最新のAmazonLinuxのAMIでEC2を構築する手順を説明しています。（初心者向け）

本記事で掲載しているテンプレートの最新版は、下記に置いてます。
https://github.com/okubo-t/aws-cloudformation
#構成図
<img src="https://qiita-image-store.s3.amazonaws.com/0/283246/ac1b131c-5272-cc32-329b-0b531d086f28.jpeg"　 width=50%>

#前提条件
下記の記事の構築手順で、VPCを構築していること。
[CloudFormationを使ってVPCを構築する]
(https://qiita.com/okubot55/items/b18a5dd5166f1ec2696c)

事前に、EC2のキーペアを生成していること。

PJPrefixの値は、同一にすること。

#構築手順
１ AWS CloudFormation管理コンソールから、スタックの作成をクリックします。
<img src="https://qiita-image-store.s3.amazonaws.com/0/283246/e0c3e7c3-f478-18c7-2dc2-7e669d1b399d.png"　 width=50%>

２ 後述のテンプレートを選択します。
<img src="https://qiita-image-store.s3.amazonaws.com/0/283246/060a4709-622a-2394-de5f-ac2103045137.png"　 width=50%>

３ 各パラメータを入力します。
<img src="https://qiita-image-store.s3.amazonaws.com/0/283246/938e8497-9fb1-4de9-0e92-d5d3e729eb23.png"　 width=70%>

|パラメータ名|用途|備考|
|:---|:---|:---|
|スタックの名前|テンプレートから作成するリソース一式の名前|例　prd-stack-vpc-20180801|
|PJPrefix|構築するプロジェクトの環境を識別するために各コンポーネントの先頭に付与する識別子|例 qiita-prd|
|KeyPairName|AmazonEC2のキーペア。利用するキーペアを選択||
|EC2InstanceName|TagのNameに設定するインスタンス名||
|EC2InstanceAMI|パラメータストアから、最新の AmazonLinuxのAMIのID を取得します。||
|EC2InstanceInstanceType|インスタンスタイプ|t2.micro(デフォルト)|
|EC2InstanceVolumeType|ボリュームのタイプ|g2（デフォルト）|
|EC2InstanceVolumeSize|ボリュームのサイズ|30(デフォルト)|
|EC2InstanceSubnet|EC2を設置するパブリックサブネット|Public SubnetA(デフォルト)|
|SSHAccessSourceIP|EC2にSSHアクセスを許可する送信元のIPアドレス||

4 後続は、デフォルトのまま次へ次へで、作成します。
<font color="Red">作成する前に、下記のチェックをつけること</font>
<font color="Red">*AWS CloudFormation によってカスタム名のついた IAM リソースが作成される場合があることを承認します。*</font>
<img src="https://qiita-image-store.s3.amazonaws.com/0/283246/7b41654e-c324-186b-8a3f-788aa070b393.jpeg"　 width=50%>

５ 状況が CREATE COMPLETEになれば、EC2の構築が完了です。
<img src="https://qiita-image-store.s3.amazonaws.com/0/283246/f919b748-e62b-9f47-0eed-a3ab499405f0.png"　 width=50%>

6 管理コンソールの下部の出力から、構築したEC2の情報を確認できます。
<img src="https://qiita-image-store.s3.amazonaws.com/0/283246/b0820f18-1925-cbf6-e959-7d479efdf6fb.png"　 width=８0%>

#テンプレート
```ec2-latest-amzlnx.yml
AWSTemplateFormatVersion: "2010-09-09"
Description:
  EC2 Instance Create

Metadata:
  "AWS::CloudFormation::Interface":
    ParameterGroups:
      - Label:
          default: "Project Name Prefix"
        Parameters:
          - PJPrefix
      - Label:
          default: "EC2Instance Configuration"
        Parameters:
          - KeyPairName
          - EC2InstanceName
          - EC2InstanceAMI
          - EC2InstanceInstanceType
          - EC2InstanceVolumeType
          - EC2InstanceVolumeSize
          - EC2InstanceSubnet
          - SSHAccessSourceIP

    ParameterLabels:
      KeyPairName:
        default: "KeyPairName"
      EC2InstanceName:
        default: "EC2 Name"
      EC2InstanceAMI:
        default: "EC2 AMI"
      EC2InstanceInstanceType:
        default: "EC2 InstanceType"
      EC2InstanceVolumeType:
        default: "EC2 VolumeType"
      EC2InstanceVolumeSize:
        default: "EC2 VolumeSize"
      EC2InstanceSubnet:
        default: "EC2 Subnet"
      SSHAccessSourceIP:
        default: "SSH AccessSourceIP"

# ------------------------------------------------------------#
# Input Parameters
# ------------------------------------------------------------# 
Parameters:
  PJPrefix:
    Type: String

#EC2Instance
  KeyPairName:
    Type: AWS::EC2::KeyPair::KeyName
    Default: ""
  EC2InstanceName:
    Type: String
    Default: "ec2-01"
  EC2InstanceAMI:
    Type: AWS::SSM::Parameter::Value<AWS::EC2::Image::Id>
    Default: "/aws/service/ami-amazon-linux-latest/amzn2-ami-hvm-x86_64-gp2"
  EC2InstanceInstanceType:
    Type: String
    Default: "t2.micro"
  EC2InstanceVolumeType:
    Type: String
    Default: "gp2"
  EC2InstanceVolumeSize:
    Type: String
    Default: "30"
  EC2InstanceSubnet:
    Type: String
    Default: "public-subnet-a"
    AllowedValues: [ public-subnet-a, public-subnet-c ]
  SSHAccessSourceIP:
    Type: String

Resources:
# ------------------------------------------------------------#
#  IAM Role for EC2
# ------------------------------------------------------------# 
  EC2IAMRole: 
    Type: "AWS::IAM::Role"
    Properties: 
      RoleName: !Sub "${PJPrefix}-${EC2InstanceName}-role" 
      AssumeRolePolicyDocument: 
        Version: "2012-10-17"
        Statement: 
          - Effect: Allow
            Principal: 
              Service: 
                - "ec2.amazonaws.com"
            Action: 
              - "sts:AssumeRole"
      Path: "/"
      ManagedPolicyArns: 
        - "arn:aws:iam::aws:policy/service-role/AmazonEC2RoleforSSM"
        - "arn:aws:iam::aws:policy/AmazonEC2ReadOnlyAccess"

  EC2InstanceProfile: 
    Type: "AWS::IAM::InstanceProfile"
    Properties: 
      Path: "/"
      Roles: 
        - Ref: EC2IAMRole
      InstanceProfileName: !Sub "${PJPrefix}-${EC2InstanceName}-profile"

# ------------------------------------------------------------#
#  EC2Instance
# ------------------------------------------------------------#
  EC2Instance:
    Type: "AWS::EC2::Instance"
    Properties:
      Tags:
        - Key: Name
          Value: !Sub "${PJPrefix}-${EC2InstanceName}"
      ImageId: !Ref EC2InstanceAMI
      InstanceType: !Ref EC2InstanceInstanceType
      KeyName: !Ref KeyPairName
      IamInstanceProfile: !Ref EC2InstanceProfile
      DisableApiTermination: false
      EbsOptimized: false
      BlockDeviceMappings:
        - DeviceName: /dev/xvda
          Ebs:
            DeleteOnTermination: true
            VolumeType: !Ref EC2InstanceVolumeType
            VolumeSize: !Ref EC2InstanceVolumeSize
      SecurityGroupIds:
        - !Ref ManagedSecurityGroup
      SubnetId: { "Fn::ImportValue": !Sub "${PJPrefix}-${EC2InstanceSubnet}" }
      UserData: !Base64 | 
        #! /bin/bash
        yum update -y

# ------------------------------------------------------------#
#  SecurityGroup for Managed
# ------------------------------------------------------------#
  ManagedSecurityGroup:
    Type: "AWS::EC2::SecurityGroup"
    Properties: 
      VpcId: { "Fn::ImportValue": !Sub "${PJPrefix}-vpc" }
      GroupName: !Sub "${PJPrefix}-managed-sg"
      GroupDescription: "-"
      Tags:
        - Key: "Name"
          Value: !Sub "${PJPrefix}-managed-sg"
# Rule
      SecurityGroupIngress:
        - IpProtocol: tcp
          FromPort: 22
          ToPort: 22
          CidrIp: !Ref SSHAccessSourceIP

# ------------------------------------------------------------#
#  ElasticIP
# ------------------------------------------------------------# 
  ElasticIP:
    Type: "AWS::EC2::EIP"
    Properties:
      Domain: vpc

  ElasticIPAssociate:
    Type: AWS::EC2::EIPAssociation
    Properties: 
      AllocationId: !GetAtt ElasticIP.AllocationId
      InstanceId: !Ref EC2Instance

# ------------------------------------------------------------#
# Output Parameters
# ------------------------------------------------------------#                
Outputs:
#EC2Instance
  EC2InstanceID:
    Value: !Ref EC2Instance
    Export:
      Name: !Sub "${PJPrefix}-${EC2InstanceName}-id"

  EC2InstancePrivateIp:
    Value: !GetAtt EC2Instance.PrivateIp
    Export:
      Name: !Sub "${PJPrefix}-${EC2InstanceName}-private-ip"

  EC2InstanceEIP:
    Value: !GetAtt EC2Instance.PublicIp
    Export:
      Name: !Sub "${PJPrefix}-${EC2InstanceName}-eip"

  EC2InstanceRoleName:
    Value: !Sub "${PJPrefix}-${EC2InstanceName}-role"
    Export:
      Name: !Sub "${PJPrefix}-${EC2InstanceName}-role-name"
```
#Point
CloudFormation テンプレートで、AWS Systems Manager Parameter Storeから、最新の Amazon Linux AMIのID を取得しています。

```
Parameters:
  EC2InstanceAMI:
    Type: AWS::SSM::Parameter::Value<AWS::EC2::Image::Id>
    Default: "/aws/service/ami-amazon-linux-latest/amzn2-ami-hvm-x86_64-gp2"
```
```    
Resources:
    Type: "AWS::EC2::Instance"
    Properties:
      ImageId: !Ref EC2InstanceAMI
```

