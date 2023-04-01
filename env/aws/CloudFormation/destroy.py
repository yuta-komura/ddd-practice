import json
import subprocess
from pprint import pprint


def execute(command):
    try:
        command = list(
            filter(lambda x: x != "", command.replace("\n", "").split(" ")))
        result = str(subprocess.run(command, stdout=subprocess.PIPE).stdout)
        result = result.replace("\\r\\n", "").replace(
            "b''", "").replace("b'", "").replace("}'", "}").replace("\\", "")
        return {} if result == "" else json.loads(result)
    except Exception as e:
        print(e)
        print("result --------------------------------------------------------------------")
        print(result)


# env = "dev"
# env = input("削除する環境名は？: ")

# ステータスが「CREATE_COMPLETE」と「ROLLBACK_COMPLETE」のスタックを削除する
command = """
aws cloudformation list-stacks --stack-status-filter CREATE_COMPLETE ROLLBACK_COMPLETE
"""
result = execute(command)
pprint(result)

i = 0
for s in result["StackSummaries"]:
    target_stack_name = s["StackName"]
    # if f"-{env}-" not in target_stack_name:
    #     continue
    command = f"""
    aws cloudformation delete-stack --stack-name {target_stack_name}
    """
    result = execute(command)
    print(f"delete-stack : {target_stack_name}")
    i += 1

print(f"削除コマンドが実行されたstackの数は: {i}個です。")
