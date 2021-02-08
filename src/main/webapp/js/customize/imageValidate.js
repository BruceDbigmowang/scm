// var left = 0;
//
// $(function(){
//     // 初始化图片验证码
//     initImageValidate();
//
//     /* 初始化按钮拖动事件 */
//     // 鼠标点击事件
//     $("#sliderInner").mousedown(function(){
//         // 鼠标移动事件
//         document.onmousemove = function(ev) {
//             left = ev.clientX;
//             if(left >= 67 && left <= 563){
//                 $("#sliderInner").css("left",(left-67)+"px");
//                 $("#slideImage").css("left",(left-67)+"px");
//             }
//         };
//         // 鼠标松开事件
//         document.onmouseup=function(){
//             document.onmousemove=null;
//             checkImageValidate();
//         };
//     });
//
// });
//
// function initImageValidate(){
//     $.ajax({
//         async : false,
//         type : "POST",
//         url : "/common/createImgValidate",
//         dataType: "json",
//         data:{
//             telephone:telephone
//         },
//         success : function(data) {
//             if(data.status < 400){
//                 // 设置图片的src属性
//                 $("#validateImage").attr("src", "data:image/png;base64,"+data.data.oriCopyImage);
//                 $("#slideImage").attr("src", "data:image/png;base64,"+data.data.newImage);
//             }else{
//                 layer.open({
//                     icon:2,
//                     title: "温馨提示",
//                     content: data.info
//                 });
//             }
//         },
//         error : function() {}
//     });
// }
//
// function exchange(){
//     initImageValidate();
// }
//
// // 校验
// function checkImageValidate(){
//     $.ajax({
//         async : false,
//         type : "POST",
//         url : "/common/checkImgValidate",
//         dataType: "json",
//         data:{
//             telephone:telephone,
//             offsetHorizontal:left
//         },
//         success : function(data) {
//             if(data.status < 400){
//                 $("#operateResult").html(data.info).css("color","#28a745");
//                 // 校验通过，调用发送短信的函数
//                 parent.getValidateCode(left);
//             }else{
//                 $("#operateResult").html(data.info).css("color","#dc3545");
//                 // 验证未通过，将按钮和拼图恢复至原位置
//                 $("#sliderInner").animate({"left":"0px"},200);
//                 $("#slideImage").animate({"left":"0px"},200);
//             }
//         },
//         error : function() {}
//     });
// }