import subprocess
import sys
import time


def animation_stdout(prefix):
    animation = "|/-\\"
    i = 0
    while i < 50:
        time.sleep(0.1)
        sys.stdout.write("\r" + prefix + animation[i % len(animation)])
        sys.stdout.flush()
        i += 1


def execute(command):
    command = list(
        filter(lambda x: x != "", command.replace("\n", " ").split(" ")))
    result = str(subprocess.run(command, stdout=subprocess.PIPE).stdout)
    return result


def create_stack_command(prefix, resource, option):
    return f"""
aws cloudformation create-stack --stack-name {prefix}-stack-{resource}
--template-body file://stack-{resource}.yml
{option}
"""


def create_stack(prefix, resource, option=""):
    command = create_stack_command(prefix, resource, option)
    execute(command)
    print(f"create stack -> {prefix}-stack-{resource} : sent")


def wait_create_stack(prefix, resource):
    while True:
        command = f"""
        aws cloudformation describe-stacks --stack-name {prefix}-stack-{resource}
        """
        result = execute(command)
        if "CREATE_COMPLETE" in result:
            sys.stdout.write(
                "\r" + f"create stack -> {prefix}-stack-{resource} : " + "completed")
            sys.stdout.flush()
            print("")
            break
        animation_stdout(
            f"create stack -> {prefix}-stack-{resource} : ")


def formation(project, env, allow_ssm):
    PJPrefix = f"{project}-{env}"

    prefix = PJPrefix
    resource = "network"
    create_stack(prefix=prefix,
                 resource=resource,
                 option=f"--parameters ParameterKey=PJPrefix,ParameterValue={PJPrefix}")
    wait_create_stack(prefix=prefix,
                      resource=resource)

    service = "auth-token"
    prefix = f"{PJPrefix}-{service}"
    resource = "redis"
    create_stack(prefix=prefix,
                 resource=resource,
                 option=f"--parameters ParameterKey=PJPrefix,ParameterValue={PJPrefix} ParameterKey=Service,ParameterValue={service}")

    service = "user"
    prefix = f"{PJPrefix}-{service}"

    resource = "web-server"
    create_stack(prefix=prefix,
                 resource=resource,
                 option=f"""
                --parameters ParameterKey=PJPrefix,ParameterValue={PJPrefix} ParameterKey=Service,ParameterValue={service} ParameterKey=AllowSSM,ParameterValue={allow_ssm}
                --capabilities CAPABILITY_NAMED_IAM
                """)

    resource = "mysql"
    create_stack(prefix=prefix,
                 resource=resource,
                 option=f"--parameters ParameterKey=PJPrefix,ParameterValue={PJPrefix} ParameterKey=Service,ParameterValue={service}")

    resource = "redis"
    create_stack(prefix=prefix,
                 resource=resource,
                 option=f"--parameters ParameterKey=PJPrefix,ParameterValue={PJPrefix} ParameterKey=Service,ParameterValue={service}")


# project = "ddd-practice"
project = input("プロジェクト名は？: ")

resource = "permission-set"
create_stack(prefix=project,
             resource=resource,
             option="--capabilities CAPABILITY_NAMED_IAM")
wait_create_stack(prefix=project,
                  resource=resource)

formation(project=project, env="prd", allow_ssm="false")
formation(project=project, env="dev", allow_ssm="true")
