import subprocess
import argparse
import sys
import os

mark = 0

def unzip(opt):
    ret = subprocess.run("unzip %s -d dest" % opt.submission, shell=True, stdout=subprocess.PIPE)
    if ret.returncode != 0:
        print("[Error] cannot unzip %s" % opt.submission)
        report_mark()
    ret = subprocess.run("find dest -name Main.java", shell=True, stdout=subprocess.PIPE)
    return os.path.dirname(ret.stdout.decode('utf-8'))

def cleanup():
    subprocess.run("rm dest/* -rf", shell=True)

def report_mark():
    print("total score:", mark)
    sys.exit(0)

def compile(src_dir):
    print("compiling your source code -- mark: 10")
    ret = subprocess.run("javac %s/*.java" % src_dir, shell=True, stdout=subprocess.PIPE)
    if ret.returncode != 0:
        print("[Error] cannot compile your source code")
        report_mark()

    global mark
    mark += 10
    print("passed!\n")


def run_testcase(src_dir, testcase, score):
    print("running testcase: %s -- mark: %d" % (testcase, score))
    input_file = os.path.join("testcases/input", testcase)
    expected_file = os.path.join("testcases/expected", testcase)
    unexpected_file = os.path.join("testcases/unexpected", testcase)
    with open(expected_file) as f:
        expected = f.readlines()
    with open(unexpected_file) as f:
        unexpected = f.readlines()
    
    cmd = "cat %s | java -cp .:mysql-connector-java-5.1.47.jar:%s Main" % (input_file, src_dir)
    try:
        output = subprocess.run(cmd, shell=True, stdout=subprocess.PIPE, timeout=5)
    except subprocess.TimeoutExpired as err:
        output = err
    output = output.stdout.decode('utf-8').lower().split("\n")
    for line in expected:
        line = line.strip()
        keywords = line.split(" ")
        if not search_line(output, keywords):
            print("failed!")
            print("\tplease check your output by running: %s" % cmd)
            print("\tplease check the expected output keywords at: %s" % expected_file)
            return
    for line in unexpected:
        line = line.strip()
        keywords = line.split(" ")
        if search_line(output, keywords):
            print("failed!")
            print("\tplease check your output by running: %s" % cmd)
            print("\tplease check the expected output keywords at: %s" % expected_file)
            return

    global mark
    mark += score
    print("passed!\n")
            

def search_line(output, keywords):
    for i, line in enumerate(output):
        if len(keywords) == len([k for k in keywords if k.lower() in line]):
            del output[i]
            return True
    return False

if __name__ == "__main__":
    parser = argparse.ArgumentParser()
    parser.add_argument("--submission", type=str, help="path to your group_number.zip")
    opt = parser.parse_args()
   
    cleanup()
    src_dir = unzip(opt)
    if len(src_dir) == 0:
        print("[Error] cannot find Main.java")
        report_mark()

    compile(src_dir) 

    with open("testcases/summary") as f:
        testcases = f.readlines()

    for testcase in testcases:
        testcase = testcase.strip().split(" ")
        score = int(testcase[1])
        testcase = testcase[0]
        run_testcase(src_dir, testcase, score)
    
    report_mark()
