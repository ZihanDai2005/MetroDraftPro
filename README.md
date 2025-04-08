# Metro Draft Pro

## Metro Exit Info PDF Generator

This project was developed as part of a real-world metro design initiative for **Fuzhou Metro**, aiming to automate the generation of **high-quality, CMYK-compatible PDF drafts** for **station exit information signage**. These PDFs were designed for large-format printing and installation in actual metro stations.

The system takes structured data from **Excel spreadsheets**—including details such as exit numbers, destinations, surrounding landmarks, transfer information, and layout grouping—and programmatically produces polished visual designs that follow **Fuzhou Metro’s signage standards**. This significantly reduced manual labor and human error previously involved in graphic design workflows.

### Background and Use Case

In actual operation, **each station’s staff is responsible for updating and maintaining the exit information**, including nearby buildings, institutions, companies, and public transportation connections. Since these details change frequently (e.g., new businesses, updated bus routes), stations must **regularly replace physical exit information stickers**.

Previously, the process required:

- Staff filling out data in Excel sheets  
- Designers manually adjusting the layout (letter spacing, bilingual alignment, font tuning)  
- Designers exporting the PDF and sending it to print

This was time-consuming, error-prone, and heavily dependent on designer availability and communication.

### Workflow Automation Solution

The project builds a **complete automated pipeline** from Excel input to CMYK-ready PDF output. Station staff simply:

1. Update the structured Excel file (which they are already familiar with)
2. Feed it into the program
3. Instantly receive a fully formatted PDF that can be **directly sent to the printing factory without any designer involved**

This automation enables:

- **Bypassing the designer step** and eliminating tedious layout work  
- **Seamless connection between data maintainer and printer**, achieving **zero-step handoff**  
- Rapid response to urgent layout updates and data changes  
- Fully compliant CMYK print files matching metro signage standards

### Key Responsibilities and Technical Highlights

- Designed and implemented the system entirely in **Java**, using **iText 7** for precise PDF layout rendering.
- Encapsulated station layout logic into modular classes like `PaidArea` and `UnpaidArea`, supporting flexible template control and extension.
- **Supports insertion of advertisement SVG or image (PNG/JPEG) logos**, in line with real-world needs of **banks or commercial malls requesting logo placement before information**.
- **Supports native Fuzhou Metro visual icons** such as **wheelchair access, elevators, bus, and rail transfer**, with automatic **arrow direction adjustment**, **icon scaling**, and **importance-based ordering**.
- Structured the output to comply with **CMYK color specifications**, ensuring compatibility with professional print processes.
- Built reusable methods for positioning elements dynamically based on input data and layout constraints.
- Ensured all outputs followed design regulations issued by **Fuzhou Metro’s design department**.

---

## 地铁出口信息 PDF 自动生成工具（已实际应用于福州地铁）

本项目用于为 **福州地铁** 出口信息设计制作系统提供技术支撑，能够自动从 Excel 数据生成 **CMYK 印刷标准**的 PDF 文件，适用于张贴于地铁站内的出入口导视贴纸。目前该工具已被用于实际车站出入口信息更新中，输出内容经过核验后直接交付印刷使用。

### 项目背景与实际使用场景

在实际地铁运营中，**每个车站的工作人员**都负责维护和更新车站出入口周边的**建筑、机构、公交、场所信息**等。由于这些信息经常发生变动（例如新开商铺、公交线路调整等），每隔一段时间，地铁站需要重新**印刷新的出口信息贴纸**来替换张贴。

以往的流程往往涉及：

- 车站将数据填入 Excel 表格  
- 设计师人工调整稿件（包括文字间距、中英文对齐、字体适配等）  
- 设计师导出 PDF 后交给印刷厂

这不仅耗时，而且容易出错，且设计师资源紧张、沟通成本高。

### 工作流自动化方案

本项目实现了**一键从 Excel 到 CMYK 印刷 PDF** 的完整闭环，车站人员仅需：

1. 维护更新 Excel 数据表格（他们本就会做的部分）；
2. 将表格输入该系统；
3. 立即获得标准化的 PDF 文件，**可直接交付厂家印刷，无需任何设计师参与**。

这一设计实现了：

- **跳过设计师** 的繁琐调整工作（如中英文对齐、排版微调等）  
- **直接连接“数据更新人”与“印刷厂”之间的工作流程**，真正做到「**0 步对接**」  
- 提高信息更新效率，尤其适用于**紧急变动下的快速贴纸替换**  
- 输出符合地铁集团规范的高精度 CMYK PDF，可直接进入印刷流程，无需二次排版

### 技术实现简要说明

- 使用 **Java + iText 7** 实现 PDF 渲染与图文排版；
- 提供 `PaidArea` 与 `UnpaidArea` 等模块类，根据车站布局灵活输出不同贴纸结构；
- **支持插入广告 SVG 或图片（PNG/JPEG）Logo**，符合 **福州地铁实际运行情况**，部分银行、商业 MALL 会要求追加 LOGO 于信息前；
- **支持轮椅、垂梯、公交、铁路图标等福州地铁原生视觉元素**，并可根据 **箭头方向自动调整图标朝向与尺寸**，并依据信息重要性自动调整图标顺序；
- 完全兼容 CMYK 输出标准，适配大幅面工业印刷；
- 所有输出均符合 **福州地铁集团设计规范** 要求。
