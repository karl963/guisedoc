<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<c:if test="${user.profile.isAllowed('ViewProducts') == true}">
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
			<input type="search" id="productPriceSearchInput" class="productSearchInputField searchInputField defaultInputField" value="Otsitava hind" maxlength="20"/>
		</td>
		
		<td class="productUnitTd">
			<input type="search" id="productUnitSearchInput" class="productSearchInputField searchInputField defaultInputField" maxlength="45" value="Otsitava Ühik"/>
		</td>
		<td class="buttonsTd">
			<input type="button" value="Otsi" class="defaultButton" id="searchForProduct"/>
			<c:if test="${user.profile.isAllowed('AddProducts') == true}">
				<input type="button" value="Lisa" class="defaultButton" id="addNewProduct"/>
			</c:if>
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
		
		<th id="priceField" class="productPriceDiv tableBorderRight numberColumn">
			<div class="productEstonianDiv">Hind</div>
			<div class="hidden productEnglishDiv">Ostu hind</div>
		</th>
		
		<th id="unitField" class="productUnitDiv tableBorderRight">
			<div class="productEstonianDiv">Ühik</div>
			<div class="hidden productEnglishDiv">Inglise ühik</div>
		</th>
		<th id="storageField" class="productDiv tableBorderRight">
			<div>Laoseis</div>
		</th>
		<th class="productDeleteTd">
			<c:if test="${user.profile.isAllowed('DeleteProducts') == true}">
				<input type="button" value="Kustuta" class="defaultButton" id="deleteProductsButton"/>
			</c:if>
		</th>
	</tr>
</thead>
<tbody>
	<c:forEach items="${products}" var="product">
	<tr class="productTableRow" id="productRow${product.ID}">
		<td class="productCodeTd tableBorderRight productRowClickable">
			${product.code}
		</td>
		
		<td class="productNameTd tableBorderRight productRowClickable">
			<div class="productEstonianDiv">${product.name}</div>
			<div class="hidden productEnglishDiv">${product.e_name}</div>
		</td>
		
		<td class="productPriceTd tableBorderRight alignRightTd productRowClickable">
			<div class="productEstonianDiv">${product.price}</div>
			<div class="hidden productEnglishDiv">${product.o_price}</div>
		</td>
		
		<td class="productUnitTd productRowClickable tableBorderRight">
			<div class="productEstonianDiv">${product.unit}</div>
			<div class="hidden productEnglishDiv">${product.e_unit}</div>
		</td>
		
		<td class="storageTd tableBorderRight productRowClickable alignRightTd">
			${product.storage}
		</td>
		
		<td class="productDeleteTd">
			<label class="productsDeleteLabel"><input type="checkbox" class="productDeleteCheckbox"/></label>
		</td>
	</tr>
	</c:forEach>
</tbody>
</table>

</c:if>