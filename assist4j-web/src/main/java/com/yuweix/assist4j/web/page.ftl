<#macro html page>
	<#if (page.pageCount > 1)>
		<div class="page_navi">
			<div class="bg-white">
				<ul>
					<#if page.hasPrev == true>
						<li class="pre1">
							<a href="javascript:void(0);" class="to_url" pn="1">&nbsp;</a>
						</li>
					<#else>
						<li class="pre1 disabled-pre">
							<a href="javascript:void(0);">&nbsp;</a>
						</li>
					</#if>
					
					<#if page.hasPrev == true>
						<li class="pre">
							<a href="javascript:void(0);" class="to_url" pn="${page.currentPage - 1}">&nbsp;</a>
						</li>
					<#else>
						<li class="pre disabled-pre">
							<a href="javascript:void(0);">&nbsp;</a>
						</li>
					</#if>
					
					<#list page.pageNos as pageNo>
						<#if pageNo == page.currentPage>
							<li class="current">
								<a href="javascript:void(0);">${pageNo}</a>
							</li>
						<#else>
							<li>
								<a href="javascript:void(0);" class="to_url" pn="${pageNo}">${pageNo}</a>
							</li>
						</#if>
					</#list>
					
					<#if page.hasNext == true>
						<li class="next">
							<a href="javascript:void(0);" class="to_url" pn="${page.currentPage + 1}">&nbsp;</a>
						</li>
					<#else>
						<li class="next disabled-next">
							<a href="javascript:void(0);">&nbsp;</a>
						</li>
					</#if>
					
					<#if page.hasNext == true>
						<li class="next1">
							<a href="javascript:void(0);" class="to_url" pn="${page.pageCount}">&nbsp;</a>
						</li>
					<#else>
						<li class="next1 disabled-next">
							<a href="javascript:void(0);">&nbsp;</a>
						</li>
					</#if>
				</ul>
			</div>
		</div>
	</#if>
	
	
	<script type="text/javascript">
		$(document).ready(function() {
			$(".to_url").on("click ", function() {
				var url = "${page.url}";
				var pageNo = $(this).attr("pn");
				
				/*检查url是否以问号结尾*/
				if (/\?$/.test(url)) {
					location.href = url + "pageNo=" + pageNo + "&pageSize=${page.pageSize}";
				} else {
					location.href = url + "&pageNo=" + pageNo + "&pageSize=${page.pageSize}";
				}
			});
		});
	</script>
</#macro>
