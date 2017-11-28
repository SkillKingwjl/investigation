var property=0
var totalUrl="/investigation";
function vilidation(){
		$.ajax({
	            url:totalUrl+"/login/validLogin",
	            type:"POST",
	            dataType:"JSON",
	            data:{},
	            async:false,
	            success:function(data){
	            if(data.userInfo){
	               	property=data.userInfo.property;
	               	if(property==2){
	               		$("#adduser").css("display","none");
	               		$("#addinput").css("display","none");
	               	}
	               	if(property==1){
	               		$("#adduser").css("display","none");
	               	}
                }else{
                	alert("您尚未登录");
                    window.location.href="login.html";
                }
            }
        });
	}	

 function skinto(to){
		if(to==1){
			window.location.href="login.html";
		}else if(to==2){
			window.location.href="content.html";
		}else if(to==3){
			window.location.href="analytics.html";
		}else if(to==4){
			window.location.href="analysis.html"
		}else if(to==5){
			window.location.href="UploadDataIndex.html";
		}else{
			window.location.href="AddUser.html";
		}
	}
	function logout(){
		$.ajax({
            url:totalUrl+"/login/loginOut",
            type:"POST",
            dataType:"JSON",
            data:{
            	
            },
            async:false,
            success:function(data){
                if(data){
               	  window.location.href="login.html";	
                }
            }
        });
	}
	function getParentList(){
		 
		 $.ajax({
           url:totalUrl+"/login/getParentList",
           type:"POST",
           dataType:"JSON",
           data:{},
           async:false,
           success:function(data){
               if(data){
              	  handlerList(data.result,0);	
               }else{
                   window.location.href="login.html";
               }
           }
       });
	}
	function handlerList(data,num){
		 var url="";
		  $.each(data,function(n,value){
		  		var id=value.id;
		  		var name=value.name;
		  		if(n==num){
		  			url+="<li role='presentation' class='active' id='"+id+"_parent'><a href='javascript:getdata("+id+",1)'>"+name+"</a></li>";
		  		}else{
		  			url+="<li role='presentation' id='"+id+"_parent'><a href='javascript:getdata("+id+",1)'>"+name+"</a></li>";
		  		}
		  		
		  });
		  $("#parentList").html(url);
	}
	
	function getUserCity(){
		$.ajax({
            url:totalUrl+"/login/getUserCity",
            type:"POST",
            dataType:"JSON",
            data:{},
            async:false,
            success:function(data){
                if(data.userInfo){
               	  handlerUserCity(data.userInfo);	
                }
            }
        });
	}
	function handlerUserCity(data){
		var city=data.city;
		var county=data.county;
		var village=data.village;
		var country=data.country;
		$("#cityList").html("<option>"+city+"</option>");
		$("#countyList").html("<option>"+county+"</option>")
		$("#villageList").html("<option>"+village+"</option>")
		$("#countryList").html("<option>"+country+"</option>")
	}
	function getYear(){
		$.ajax({
            url:totalUrl+"/login/getYear",
            type:"POST",
            dataType:"JSON",
            data:{
            	
            },
            async: false,
            success:function(data){
                if(data){
               	  handleYear(data.result);	
                }
            }
        });
	}
	function handleYear(data){
		var url="";
		$.each(data,function(n,value){
		  		var year=value.year;	
		  		if(year){
		  			if(n==0){
		  				url+="<option selected>"+year+"</option>";
		  			}else{
		  				url+="<option>"+year+"</option>";
		  			}
		  				
		  		}
		  });
		  $("#setYear").html(url);
	}
	