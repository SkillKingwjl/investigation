	var local_id=1;
	var total_page=2;
	function getdata(parent_id,page){
		local_id=parent_id;
		$("#parentList li").removeClass();
		$("#"+local_id+"_parent").addClass("active")
		if(page<1){
			alert("没有上一页");
			return 
		}
		var city=$("#cityList option:selected").text();
		var county=$("#countyList option:selected").text();
		var village=$("#villageList option:selected").text();
		var country=$("#countryList option:selected").text();
		var year=$("#setYear option:selected").text();
		if(!city||city.indexOf('请选择')>-1){
			city="";
		}
		if(!county||county.indexOf('请选择')>-1){
			county="";
		}
		if(!village||village.indexOf('请选择')>-1){
			village="";
		}
		if(!country||country.indexOf('请选择')>-1){
			country="";
		}
		if(year==""){
			year=0;
		}
		 $.ajax({
            url:totalUrl+"/login/getVillageInformation",
            type:"POST",
            dataType:"JSON",
            async: false,
            data:{
            	"parent_id":parent_id,
            	"page":page,
            	"city":city,
            	"county":county,
            	"village":village,
            	"country":country,
            	"year":year
            },
            success:function(data){
                if(data.result){
                	var page_data=data.result.page
                	total_page=page_data.totalPage;
	                if(page_data.list){
	                	handlerData(page_data.list);
	                	setpage(parent_id,page)	
	                }else{
	                	handlerDataClear();
	                }
	                $("li[id*=_parent]").attr("class","");
	                $("#"+parent_id+"_parent").attr("class","active");
	                if(page>total_page&&total_page!=0){
            			alert("没有下一页");
            			return ;
            		}
                	if(total_page==0){
            			alert("暂无数据");
            			return ;
            		}
                	var locations=data.result.locations;
                	$("#location").html(locations);
                }else{
                    window.location.href="login.html";
                }
            }
        });
	}
	function setpage(parent_id,pageNum){
		var prepage=pageNum-1;
		var nextpage=pageNum+1;		
		$("#next").attr("href","javascript:getdata("+parent_id+","+nextpage+")");
		$("#pre").attr("href","javascript:getdata("+parent_id+","+prepage+")");
	}
	
	function handlerData(data){
		  var url="<table class='table table-bordered table-hover table-responsive'>"
			  url+="<tr><th>表格属性</th><th>属性值</th><th>属性详情</th></tr>";
		   $("#data").append(url);
		  $.each(data,function(n,value){
		  		var id=value.id;
		  		var parent_id=value.parent_id;
		  		var name=value.name;
		  		var attr=value.attr;
		  		var result=value.result;
		  		url+="<tr><td>"+name+"</td><td>"+result+"</td><td>"+attr+"</td></tr>";
		  });
		  url+="</table>"
		  $("#data").html(url);
	}
	
	function handlerDataClear(){
		var url="<table class='table table-bordered table-hover table-responsive'>"
			url+="<tr><th>表格属性</th><th>属性值</th><th>属性详情</th></tr>";
		    url+="</table>"
	    $("#data").html(url);
	}
	
	function getParentList(year){
		 
		 $.ajax({
            url:totalUrl+"/login/getParentList",
            type:"POST",
            dataType:"JSON",
            data:{},
            async: false,
            success:function(data){
                if(data.result){
               	  handlerList(data.result,1);	
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
		  			url+="<li role='presentation' class='active' id='"+id+"_parent'><a href='javascript:getdata("+id+",1,20)'>"+name+"</a></li>";
		  		}else{
		  			url+="<li role='presentation' id='"+id+"_parent'><a href='javascript:getdata("+id+",1,20)'>"+name+"</a></li>";
		  		}
		  		
		  });
		  $("#parentList").html(url);
	}
	
	
	
	function getCity(){
		$.ajax({
            url:totalUrl+"/login/getCity",
            type:"POST",
            dataType:"JSON",
            data:{},
            async: false,
            success:function(data){
                if(data){
               	  handleCity(data.result,"cityList");	
                }
            }
        });
	}
	function getCounty(){
		var city=$("#cityList option:selected").text();
		if(!city){
			return 
		}
		$.ajax({
            url:totalUrl+"/login/getCounty",
            type:"POST",
            dataType:"JSON",
            async: false,
            data:{
            	"city":city
            },
            success:function(data){
                if(data){
               	  handleCity(data.result,"countyList");	
                }
            }
        });
	}
	function getVillage(){
		var county=$("#countyList option:selected").text();
		if(!county){
			return 
		}
		$.ajax({
            url:totalUrl+"/login/getVillage",
            type:"POST",
            dataType:"JSON",
            async: false,
            data:{
            	"county":county
            },
            success:function(data){
                if(data){
               	  handleCity(data.result,"villageList");	
                }
            }
        });
	}
	function getCountry(){
		var village=$("#villageList option:selected").text();
		if(!village){
			return 
		}
		$.ajax({
            url:totalUrl+"/login/getCountry",
            type:"POST",
            dataType:"JSON",
            async: false,
            data:{
            	"village":village
            },
            success:function(data){
                if(data){
               	  handleCity(data.result,"countryList");	
                }
            }
        });
	}
	function handleCity(data,id){
		var url=""
		if(id=="cityList"){
			url+="<option>请选择市</option>"
		}else if(id=="countyList"){
			url+="<option>请选择县</option>"
		}else if(id=="villageList"){
			url+="<option>请选择乡</option>"
		}else{
			url+="<option>请选择村</option>"
		}
		$.each(data,function(n,value){
		  		var name="";
		  		if(id=="cityList"){
					name=value.city
				}else if(id=="countyList"){
					name=value.county
				}else if(id=="villageList"){
					name=value.village
				}else{
					name=value.country
				}				
		  		if(name){
		  			url+="<option>"+name+"</option>";	
		  		}
		  });
		  $("#"+id).html(url);
		  handleother(id);
	}
	function handleother(id){
		var urls="<option>请选择市</option>"
		var urls1="<option>请选择县</option>"
		var urls2="<option>请选择乡</option>"
		var urls3="<option>请选择村</option>"
		if(id=="cityList"){
			 $("#countyList").html(urls1);
			 $("#villageList").html(urls2);
			 $("#countryList").html(urls3);
		}else if(id=="countyList"){
			 $("#villageList").html(urls2);
			 $("#countryList").html(urls3);
		}else if(id=="villageList"){
			$("#countryList").html(urls3);
		}
		 getDataByAddress();
	}
	
	function getDataByAddress(){
		getdata(local_id,1)
	}
	function searchData(){
		getdata(local_id,1)
	}
	