<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<div id="productsSideDiv">
	<input type="button" value="EST" id="productsInEstonian" class="selectedLanguageButton"/>
	<input type="button" value="ENG" id="productsInEnglish" class="defaultButton"/>
</div>

<table id="searchTable">
	<tr id="searchRow">
		<td class="productCodeTd">
			<input type="search" id="productCodeSearchInput" class="productSearchInputField searchInputField defaultInputField" maxlength="45" value="Otsitava kood"/>
		</td>
			
		<td class="productNameTd">
			<input type="search" id="productNameSearchInput" class="productSearchInputField searchInputField defaultInputField" maxlength="100" value="Otsitava nimetus"/>
		</td>
		
		<td class="productPriceTd">
			<input type="search" id="productPriceSearchInput" class="productSearchInputField searchInputField defaultInputField" value="Otsitava hind"/>
		</td>
		
		<td class="productUnitTd">
			<input type="search" id="productUnitSearchInput" class="productSearchInputField searchInputField defaultInputField" maxlength="20" value="Otsitava Ühik"/>
		</td>
		<td class="buttonsTd">
			<input type="button" value="Otsi" class="defaultButton" id="searchForProduct"/>
			<input type="button" value="Lisa" class="defaultButton" id="addNewProduct"/>
		</td>
	</tr>
</table>
<table id="productsTable" >
<thead>
	<tr class="tableHeaderRow">
		
		<th id="codeField" class="productCodeDiv tableBorderRight">Kood</th>
		
		<th id="nameField" class="productNameDiv tableBorderRight">
			<div class="productEstonianDiv" >Nimetus</div>
			<div class="hidden productEnglishDiv" >Inglise nimetus</div>
		</th>
		
		<th id="priceField" class="productPriceDiv tableBorderRight">
			<div class="productEstonianDiv">Hind</div>
			<div class="hidden productEnglishDiv">Ostu hind</div>
		</th>
		
		<th id="unitField" class="productUnitDiv tableBorderRight">
			<div class="productEstonianDiv">Ühik</div>
			<div class="hidden productEnglishDiv">Inglise ühik</div>
		</th>
		<th id="storageField" class="productDiv">
			<div>Laoseis</div>
		</th>
	</tr>
</thead>
<tbody>
	<c:forEach items="${products}" var="product">
	<tr class="productTableRow">
		<td class="productCodeTd tableBorderRight">
			<div class="productID hidden">${product.ID}</div>
			<div >${product.code}</div>
		</td>
		
		<td class="productNameTd tableBorderRight">
			<div class="productEstonianDiv">${product.name}</div>
			<div class="hidden productEnglishDiv">${product.e_name}</div>
		</td>
		
		<td class="productPriceTd tableBorderRight">
			<div class="productEstonianDiv">${product.price}</div>
			<div class="hidden productEnglishDiv">${product.o_price}</div>
		</td>
		
		<td class="productUnitTd" colspan="2">
			<div class="productEstonianDiv">${product.unit}</div>
			<div class="hidden productEnglishDiv">${product.e_unit}</div>
		</td>
	</tr>
	</c:forEach>
</tbody>
</table>
