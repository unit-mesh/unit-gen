import json


def process_jsonl(input_file, output_file):
    with open(input_file, 'r') as f:
        lines = f.readlines()

    filtered_records = []

    for line in lines:
        data = json.loads(line)
        if data["lang"] == "java":
            filtered_record = {
                "instruction": data["problem"],
                "output": data["solution"]
            }
            filtered_records.append(filtered_record)

    # write down 1000 records to output file
    filtered_records = filtered_records
    with open(output_file, 'w') as f:
        for record in filtered_records:
            f.write(json.dumps(record) + '\n')



def merge_jsonl(input_file1, input_file2, input_file3, output_file):
    with open(input_file1, 'r') as f1, open(input_file2, 'r') as f2, open(input_file3, 'r') as f3:
        lines1 = f1.readlines()
        lines2 = f2.readlines()
        lines3 = f3.readlines()

    records = []

    # Process the records from the first file
    for line in lines1[:4000]:
        data = json.loads(line)
        records.append(data)


    random.shuffle(lines2)
    # Process the records from the second file
    for line in lines2[:1000]:
        data = json.loads(line)
        records.append(data)

    random.shuffle(lines3)
    # Process the records from the third file
    for line in lines3[:1000]:
        data = json.loads(line)
        records.append(data)

    random.shuffle(records)
    # Write the merged records to the output file
    with open(output_file, 'w') as f:
        for record in records:
            f.write(json.dumps(record) + '\n')

# 用法示例
input_file = 'data-oss_instruct-decontaminated.jsonl'
oss_instruction = 'java-oss_instruction.jsonl'
summary_file = 'summary.jsonl'
merged_file = 'output.jsonl'
test_summary_file = 'test-summary.jsonl'

# 处理输入文件，过滤Java记录
process_jsonl(input_file, oss_instruction)

# 合并输出文件和结果文件
merge_jsonl(oss_instruction, summary_file, test_summary_file, merged_file)
