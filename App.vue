<template>
  <div id="app">
    <div class="hedaer">
      <h1>用户故事质量检测系统</h1>
    </div>
    <div class="steps">
      <el-steps :active="active" simple>
        <el-step title="结构检测" icon="el-icon-edit"></el-step>
        <el-step title="句法检测" icon="el-icon-upload"></el-step>
        <el-step title="语义检测" icon="el-icon-picture"></el-step>
      </el-steps>
    </div>
    <div class="leftmain" ref="leftmain">
      <div class="nav">
        <el-button type="primary" plain @click="handleButtonClick">上传</el-button>
        <el-button type="primary" plain @click="delinputdata">清空故事</el-button>
        <el-button type="primary" plain @click="changestyle">检测报告</el-button>
        <!-- <el-button type="primary" plain @click="drawer = true">检测报告</el-button> -->
        <el-button type="primary" plain @click="getstruAnalysis">结构检测</el-button>
        <el-button type="primary" plain @click="getparse" :disabled="active == 1 ? false : true">句法检测</el-button>
        <el-button type="primary" plain @click="getsemparse" :disabled="active == 2 ? false : true">语义检测</el-button>
      </div>
      <div class="main">
        <span class="main-header">请输入用户故事，可在线编辑</span>
        <div class="userinput" ref="main">
          <div v-for="(item, index) in inputData" :key="index" class="inputRow">
            <span>{{ index + 1 }}.</span>
            <textarea class="no-resize" v-model="inputData[index]" @keydown.enter="handleEnter($event, index)"
              @keydown.backspace="handleBackspace($event, index)" @focus="deleback(index)" @input="getNumberOfRows"
              ref="textareas"></textarea>
          </div>
        </div>
      </div>
      <div class="problem" ref="problem">
        <span class="problem-header">问题：{{ active == 0 ? '结构检测' : active == 1 ? '句法检测' : active == 2 ? '语义检测' : ''
          }}</span>
        <div class="problem-main">
          <div class="problem-main-header">
            <div class="problem-roms">
              <span>行数</span>
            </div>
            <div class="problem-inform">
              <span>错误信息</span>
            </div>
          </div>
          <div class="problem-list" v-for="(item, index) in problemlist" :key="index">
            <div class="problem-list-item" @dblclick.stop="Determinerows(item, index)">
              <div class="problem-roms">
                <span>{{ !ifsemparse ? "Line" + item.usline : 'Line' + item.usline + '&' + item.usline2 }}</span>
              </div>
              <div class="problem-inform">
                <span>{{ item.probleminfo }}</span>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
    <div class="footer">软件需求研究分析小组</div>
    <div class="reportmain" ref="rightmain">
      <div class="report-header">
        <h1>检测报告</h1>
      </div>
      <div class="report-main">
        <div class="report-left">
          <div class="report-item report-item-mar">
            <div class="report-num">{{ totalProblems }}</div>
            <div class="report-title">总问题</div>
          </div>
          <div class="report-item">
            <div class="report-num back-jinggao">{{ warningCount }}</div>
            <div class="report-title">警告</div>
          </div>
        </div>
        <div class="report-right">
          <div class="report-item report-item-mar">
            <div class="report-num back-quexian">{{ errorCount }}</div>
            <div class="report-title">缺陷</div>
          </div>
          <div class="report-item">
            <div class="report-num back-wanmei">{{ perfectCount }}</div>
            <div class="report-title">完美</div>
          </div>
        </div>
      </div>
      <div class="falseifsemparse" v-if="!ifsemparse">
        <div class="report-prm" v-for="(item, index) in reportlist" :key="index">
          <div class="prm-item">
            <div class="item-title">
              <span>US{{ item.usline }}</span>
              <el-input type="text" v-model="inputData[item.usline - 1]" :disabled="item.disabled"
                @change="changeinput(item)">
                <i class="el-input__icon el-icon-edit" slot="suffix" @click="changeinputdisabled(item, index)"></i>
              </el-input>
            </div>
            <div class="describe">
              <div class="describe-cor" :class="item.messages == 'warming' ? 'warming' : 'error'"></div>
              <div class="describe-main">
                <div class="zhunze">
                  <span>所违反的准则:{{ item.violatedGuidelines }}</span>
                </div>
                <div class="zhunze">
                  <span>问题描述:{{ item.errors }}</span>
                </div>
                <div class="zhunze">
                  <span>建议:{{ item.suggestions }}</span>
                </div>
              </div>
            </div>
          </div>
          <!-- <div class="prm-item">
          <div
            class="item-title"
          >#I As a user, I want to attach multiple files at once to a log book page</div>
          <div class="describe">
            <div class="describe-cor"></div>
            <div class="describe-main">
              <div class="zhunze">
                <span>所违反的准则</span>
              </div>
              <div class="zhunze">
                <span>问题描述:</span>
              </div>
              <div class="zhunze">
                <span>建议:</span>
              </div>
            </div>
          </div>
          </div>-->
        </div>
      </div>
      <div class="trueifsemparse" v-if="ifsemparse">
        <div class="report-prm" v-for="(item, index) in reportlist" :key="index">
          <div class="prm-item">
            <div class="item-title">
              <span>US{{ item.usline }}</span>
              <el-input type="text" v-model="inputData[item.usline - 1]" :disabled="item.disabled"
                @change="changeinput(item)">
                <i class="el-input__icon el-icon-edit" slot="suffix" @click="changeinputdisabled(item, index, 1)"></i>
              </el-input>
            </div>
            <div class="item-title">
              <span>US{{ item.usline2 }}</span>
              <el-input type="text" v-model="inputData[item.usline2 - 1]" :disabled="item.disabled"
                @change="changeinput(item)">
                <i class="el-input__icon el-icon-edit" slot="suffix" @click="changeinputdisabled(item, index, 2)"></i>
              </el-input>
            </div>
            <div class="describe">
              <div class="describe-cor" :class="item.messages == 'warming' ? 'warming' : 'error'"></div>
              <div class="describe-main">
                <div class="zhunze">
                  <span>所违反的准则:{{ item.violatedGuidelines }}</span>
                </div>
                <div class="zhunze">
                  <span>问题描述:{{ item.errors }}</span>
                </div>
                <div class="zhunze">
                  <span>建议:{{ item.suggestions }}</span>
                </div>
              </div>
            </div>
          </div>
          <!-- <div class="prm-item">
          <div
            class="item-title"
          >#I As a user, I want to attach multiple files at once to a log book page</div>
          <div class="describe">
            <div class="describe-cor"></div>
            <div class="describe-main">
              <div class="zhunze">
                <span>所违反的准则</span>
              </div>
              <div class="zhunze">
                <span>问题描述:</span>
              </div>
              <div class="zhunze">
                <span>建议:</span>
              </div>
            </div>
          </div>
          </div>-->
        </div>
      </div>
    </div>
    <!-- <el-drawer title="检测报告" :visible.sync="drawer" :with-header="false" size="35%"></el-drawer> -->
  </div>
</template>

<script>
import { getstruAnalysis, getparse, getsemparse } from "./api/api";
import * as XLSX from "xlsx";
export default {
  name: "VueEnglishApp",

  data() {
    return {
      inputData: [""],
      beforeEnter: "",
      afterEnter: "",
      drawer: false,
      active: 0,
      mokuai: false,
      problemlist: [],
      reportlist: [],
      errorCount: "",
      perfectCount: "",
      totalProblems: "",
      warningCount: "",
      delline: "",
      ifsemparse: false
    };
  },

  mounted() { },

  methods: {
    handleEnter(event, index) {
      event.preventDefault(); // 阻止回车键的默认行为
      const currentInput = this.inputData[index];
      const remainingText = currentInput.substring(event.target.selectionEnd);
      const enteredText = currentInput.substring(
        0,
        event.target.selectionStart
      );
      this.inputData[index] = enteredText;
      this.inputData.splice(index + 1, 0, remainingText);
      this.$nextTick(() => {
        const newInput = this.$el.querySelectorAll(".inputRow textarea")[
          index + 1
        ];
        newInput.focus();
        newInput.selectionStart = newInput.selectionEnd = 0;
      });
    },
    handleBackspace(event, index) {
      if (index === 0) {
        return;
      }
      const textarea = this.$refs.textareas[index];
      if (textarea.selectionStart === 0 && textarea.selectionEnd === 0) {
        event.preventDefault(); // 阻止默认的删除操作
        this.inputData[index - 1] += this.inputData[index];
        this.inputData.splice(index, 1);
        this.$nextTick(() => {
          const previousRowTextarea = this.$refs.textareas[index - 1];
          previousRowTextarea.selectionStart = previousRowTextarea.value.length;
          previousRowTextarea.selectionEnd = previousRowTextarea.value.length;
          previousRowTextarea.focus();
        });
      }
    },
    //双击行数高亮
    Determinerows(item, index) {
      let obox = document.querySelectorAll(".inputRow textarea");
      let oprom = document.querySelectorAll(".problem-list-item");
      obox.forEach(ele => {
        ele.classList.remove("backgurd");
      });
      oprom.forEach(ele => {
        ele.classList.remove("backgurd");
      });
      this.delline = item.usline - 1;
      obox[item.usline - 1].classList.add("backgurd");
      event.currentTarget.classList.add("backgurd");
    },
    deleback(index) {
      if (this.delline == index) {
        let obox = document.querySelectorAll(".inputRow textarea");
        let oprom = document.querySelectorAll(".problem-list-item");
        obox.forEach(ele => {
          ele.classList.remove("backgurd");
        });
        oprom.forEach(ele => {
          ele.classList.remove("backgurd");
        });
      }
    },
    delinputdata() {
      this.inputData = [""];
    },
    getNumberOfRows(event) {
      event.target.style.height = 16 + "px";
      event.target.style.height = event.target.scrollHeight + "px";
      // const textarea = this.$refs.textarea;
      // const lineHeight = parseInt(getComputedStyle(textarea.target).lineHeight);
      // const contentHeight = event.target.scrollHeight;
      // const numRows = Math.floor(contentHeight / lineHeight);
      // console.log(numRows)
    },
    //右边弹出框
    changestyle() {
      if (!this.mokuai) {
        this.$refs.leftmain.style.width = "68%";
        this.$refs.rightmain.style.display = "block";
        this.$refs.main.style.height = "65vh";
        this.$refs.problem.style.display = "none";
        this.$refs.rightmain.style.width = "30%";
        this.mokuai = true;
        let lefttext = document.querySelectorAll(".no-resize");
        lefttext.forEach(ele => {
          ele.style.height = 16 + "px";
          ele.style.height = ele.scrollHeight + "px";
        });
      } else {
        this.$refs.leftmain.style.width = "100%";
        this.$refs.rightmain.style.display = "none";
        this.$refs.problem.style.display = "block";
        this.$refs.main.style.height = "40vh";
        this.mokuai = false;
        let lefttext = document.querySelectorAll(".no-resize");
        lefttext.forEach(ele => {
          ele.style.height = 16 + "px";
          ele.style.height = ele.scrollHeight + "px";
        });
      }
    },
    //改变input框禁用状态高亮
    changeinputdisabled(e, index, tice = 1) {
      if (tice == 1) {
        if (e.disabled) {
          let obox = document.querySelectorAll(".inputRow textarea");
          event.target.classList.add("backgurd");
          obox[e.usline - 1].classList.add("backgurd");
          e.disabled = false;
        } else {
          e.disabled = true;
          event.target.classList.remove("backgurd");
          obox[e.usline - 1].classList.remove("backgurd");
        }
      } else {
        if (e.disabled) {
          let obox = document.querySelectorAll(".inputRow textarea");
          event.target.classList.add("backgurd");
          obox[e.usline2 - 1].classList.add("backgurd");
          e.disabled = false;
        } else {
          e.disabled = true;
          event.target.classList.remove("backgurd");
          obox[e.usline2 - 1].classList.remove("backgurd");
        }
      }
    },
    //结构检测
    getstruAnalysis() {
      let data = [];
      if (this.inputData[0] == "") {
        this.$message({
          message: "请输入用户故事",
          type: "warning"
        });
      } else {
        this.ifsemparse = false;
        data = this.inputData;
        getstruAnalysis(data)
          .then(response => {
            this.problemlist = [];
            this.reportlist = [];
            console.log(response.data, '222');
            for (let i = 0; i < response.data.usLine.length; i++) {
              let obj = {
                usline: response.data.usLine[i],
                probleminfo:
                  response.data.errors[i] + response.data.suggestions[i]
              };
              let reportobj = {
                usline: response.data.usLine[i],
                errors: response.data.errors[i],
                violatedGuidelines: response.data.violatedGuidelines[i],
                suggestions: response.data.suggestions[i],
                messages: response.data.messages[i],
                disabled: true
              };
              this.problemlist.push(obj);
              this.reportlist.push(reportobj);
            }
            this.errorCount = response.data.errorCount;
            this.perfectCount = response.data.perfectCount;
            this.totalProblems = response.data.totalProblems;
            this.warningCount = response.data.warningCount;
            if (this.totalProblems == 0) {
              this.$message({
                message: "请进行句法检测",
                type: "success"
              });
              this.active = 1;
            }
          })
          .catch(error => {
            // 处理请求错误
            console.log(error);
            // console.error(error);
          });
      }
    },
    //jufa检测
    getparse() {
      let data = [];
      if (this.inputData[0] == "") {
        this.$message({
          message: "请输入用户故事",
          type: "warning"
        });
      } else {
        this.ifsemparse = false;
        data = this.inputData;
        getparse(data)
          .then(response => {
            this.problemlist = [];
            this.reportlist = [];
            console.log(response.data, '11111');
            for (let i = 0; i < response.data.usLine.length; i++) {
              let obj = {
                usline: response.data.usLine[i],
                probleminfo:
                  response.data.errors[i] + response.data.suggestions[i]
              };
              let reportobj = {
                usline: response.data.usLine[i],
                errors: response.data.errors[i],
                violatedGuidelines: response.data.violatedGuidelines[i],
                suggestions: response.data.suggestions[i],
                messages: response.data.messages[i],
                disabled: true
              };
              this.problemlist.push(obj);
              this.reportlist.push(reportobj);
            }
            this.errorCount = response.data.errorCount;
            this.perfectCount = response.data.perfectCount;
            this.totalProblems = response.data.totalProblems;
            this.warningCount = response.data.warningCount;
            if (this.totalProblems == 0) {
              this.$message({
                message: "请进行语义检测",
                type: "success"
              });
              this.active = 2;
            }
          })
          .catch(error => {
            // 处理请求错误
            console.error(error);
          });
      }
    },
    //语义检测
    getsemparse() {
      let data = [];
      if (this.inputData[0] == "") {
        this.$message({
          message: "请输入用户故事",
          type: "warning"
        });
      } else {
        this.ifsemparse = true;
        data = this.inputData;
        getsemparse(data)
          .then(response => {
            this.problemlist = [];
            this.reportlist = [];
            console.log(response);
            for (let i = 0; i < response.data.modelConflicts.length; i++) {
              let obj1 = {
                usline: response.data.modelConflicts[i].usline1,
                usline2: response.data.modelConflicts[i].usline2,
                probleminfo:
                  response.data.modelConflicts[i].errors +
                  response.data.modelConflicts[i].suggestions
              };
              let reportobj = {
                usline: response.data.modelConflicts[i].usline1,
                usline2: response.data.modelConflicts[i].usline2,
                errors: response.data.modelConflicts[i].errors,
                violatedGuidelines:
                  response.data.modelConflicts[i].violatedGuidelines,
                suggestions: response.data.modelConflicts[i].suggestions,
                messages: response.data.modelConflicts[i].messages,
                disabled: true
              };
              this.problemlist.push(obj1);
              this.reportlist.push(reportobj);
            }
            this.errorCount = response.data.modelConflicts.length > 0 ?
              response.data.modelConflicts[
                response.data.modelConflicts.length - 1
              ].errorCount : 0; //
            this.perfectCount =
              response.data.modelConflicts1[
                response.data.modelConflicts1.length - 1
              ].perfectCount;
            this.totalProblems = response.data.modelConflicts.length > 0 ?
              response.data.modelConflicts[
                response.data.modelConflicts.length - 1
              ].totalProblems : 0;
            this.warningCount = response.data.modelConflicts.length > 0 ?
              response.data.modelConflicts[
                response.data.modelConflicts.length - 1
              ].warningCount : 0;
            console.log(response.data.modelConflicts, 'sss');
            if (this.totalProblems == 0) {
              this.$message({
                message: "没有错误了",
                type: "success"
              });
              this.active = 3;
            }
          })
          .catch(error => {
            // 处理请求错误
            console.error(error);
          });
      }
    },
    handleButtonClick() {
      const fileInput = document.createElement("input");
      fileInput.type = "file";
      fileInput.accept = ".xlsx, .xls";
      fileInput.addEventListener("change", this.handleFileChange);
      fileInput.click();
    },
    handleFileChange(event) {
      const file = event.target.files[0];
      const reader = new FileReader();

      reader.onload = e => {
        const data = new Uint8Array(e.target.result);
        const workbook = XLSX.read(data, { type: "array" });
        const worksheet = workbook.Sheets[workbook.SheetNames[0]];
        const jsonData = XLSX.utils.sheet_to_json(worksheet, { header: 1 });

        this.inputData = jsonData.map(row => row[0]);
        // console.log(this.excelData);
      };

      reader.readAsArrayBuffer(file);
    }
  }
};
</script>
<style>
#app {
  font-family: "Helvetica Neue", Helvetica, "PingFang SC", "Hiragino Sans GB",
    "Microsoft YaHei", "微软雅黑", Arial, sans-serif;
  /* font-family: Avenir, Helvetica, Arial, sans-serif; */
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  /* text-align: center; */
  color: #2c3e50;
  position: relative;
}

.hedaer {
  text-align: center;
  width: 100%;
  /* border: 1px solid #333; */
  border-radius: 5px;
  box-shadow: 0 3px 8px rgba(3, 21, 34, 0.05);
  margin-bottom: 10px;
}

.hedaer h1 {
  display: inline-block;
  margin-left: 10px;
}

.nav {
  width: 84%;
  padding: 10px 8%;
  margin-bottom: 10px;
}

.steps {
  width: 100%;
}

.no-resize {
  padding: 0;
  line-height: 16px;
  height: 16px;
  width: 100%;
  border-style: none;
  resize: none;
  outline: none;
}

.inputRow {
  font-size: 12px;
}

.userinput {
  border: 2px solid #409eff82;
  border-radius: 5px;
  width: 100%;
  height: 40vh;
  overflow: auto;
  padding-left: 4px;
}

.userinput::-webkit-scrollbar {
  width: 4px;
}

.userinput::-webkit-scrollbar-thumb {
  border-radius: 10px;
  background: rgba(0, 0, 0, 0.2);
}

.userinput::-webkit-scrollbar-track {
  border-radius: 0;
  background: rgba(0, 0, 0, 0.1);
}

.inputRow {
  display: flex;
  line-height: 16px;
  /* align-items: center; */
}

.main {
  margin-bottom: 10px;
  padding: 0 8%;
}

.main-header {
  color: #999;
  font-size: 12px;
}

.problem {
  /* width: 100%; */
  /* border: 1px solid #333; */
  border-radius: 5px;
  padding: 0 8%;
}

.problem-main {
  overflow: auto;
}

.problem-main::-webkit-scrollbar {
  width: 4px;
}

.problem-main::-webkit-scrollbar-thumb {
  border-radius: 10px;
  background: rgba(0, 0, 0, 0.2);
}

.problem-main::-webkit-scrollbar-track {
  border-radius: 0;
  background: rgba(0, 0, 0, 0.1);
}

.problem-header {
  display: inline-block;
  width: 100%;
  box-sizing: border-box;
  padding-left: 10px;
  /* border-bottom: 1px solid #666; */
  font-weight: 700;
}

.problem-main {
  height: 40vh;
  border: 2px solid #409eff82;
  border-radius: 5px;
}

.jindu {
  position: fixed;
  right: 0;
  top: 0;
}

.reportmain {
  display: none;
  /* width: 30%; */
  position: absolute;
  top: 238px;
  right: 66px;
  height: 60vh;
  overflow-y: auto;
}

.reportmain::-webkit-scrollbar {
  width: 4px;
}

.reportmain::-webkit-scrollbar-thumb {
  border-radius: 10px;
  background: rgba(0, 0, 0, 0.2);
}

.reportmain::-webkit-scrollbar-track {
  border-radius: 0;
  background: rgba(0, 0, 0, 0.1);
}

.report-main {
  /* width: 100%; */
  display: flex;
  justify-content: space-around;
  border: 2px solid #333;
  padding: 2% 0;
}

.report-item {
  width: 100%;
  border: 1.5px solid #333;
  display: flex;
  /* justify-content: space-around; */
  align-items: center;
  padding: 5px;
  /* padding: 2px 10px;
  margin-top: 1%;
  margin-bottom: 2%; */
}

.report-num {
  width: 30px;
  height: 30px;
  line-height: 30px;
  text-align: center;
  background: #999;
  border-radius: 50%;
  color: #ffffff;
  margin-right: 5px;
}

.item-title {
  display: flex;
  align-items: center;
}

.back-jinggao {
  background: #f09f49;
}

.back-quexian {
  background: #f65850;
}

.back-wanmei {
  background: #9fee66;
}

.report-header {
  border: 2px solid #333;
  text-align: center;
  margin-bottom: 3px;
}

.report-header h1 {
  font-size: 20px;
}

.describe {
  display: flex;
  padding-left: 10px;
  align-items: center;
}

.describe-cor {
  width: 20px;
  height: 40px;
  margin-right: 2px;
}

.error {
  background: #f65850;
}

.warming {
  background: #f09f49;
}

.prm-item {
  margin-top: 4px;
  border-bottom: 1px solid #ddd;
}

.problem-main-header {
  display: flex;
  padding: 10px 2%;
}

.problem-roms {
  width: 8%;
  /* padding: 10px 0; */
  border-bottom: 1px solid #ddd;
  margin-right: 5px;
  text-align: center;
}

.problem-inform {
  width: 80%;
  /* padding: 10px 0; */
  border-bottom: 1px solid #ddd;
}

.zhunze {
  font-size: 12px;
  line-height: 20px;
}

.report-left,
.report-right {
  width: 40%;
}

.report-item-mar {
  margin-bottom: 7%;
}

.problem-list-item {
  display: flex;
  padding: 3px 2%;
}

.backgurd {
  background: #dddddd;
}

.footer {
  margin-top: 5px;
  text-align: center;
  height: 63px;
  line-height: 63px;
  background-color: #f1f4f7;
  padding: 0;
  width: 100%;
  z-index: 999;
}
</style>
