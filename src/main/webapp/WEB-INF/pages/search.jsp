<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<jsp:useBean id="products" type="java.util.ArrayList" scope="request"/>
<tags:master pageTitle="Advanced search page">
    <h1>Advanced search page</h1>
    <form method="post">
    <table>
    <tags:searchForm name="code" label="Product code" errors="${errors}"></tags:searchForm>
    <tags:searchForm name="minPrice" label="Min price" errors="${errors}"></tags:searchForm>
    <tags:searchForm name="maxPrice" label="Max Price" errors="${errors}"></tags:searchForm>
    <tags:searchForm name="minStock" label="Min stock" errors="${errors}"></tags:searchForm>
    </table>
    <p>
        <button>Search</button>
    </p>
    </form>
    <h3>${message}</h3>
    <table>
    <thead>
    <tr>
        <td>Image</td>
        <td>Description</td>
        <td class="price">Price</td>
    </tr>
    </thead>
        <c:forEach var="product" items="${products}" varStatus="status">
    <tr>
    <td>
        <img class="product-tile" src="${product.imageUrl}">
    </td>

    <td>
        <a href="${pageContext.servletContext.contextPath}/products/${product.id}">
                ${product.description}
        </a>
    </td>
        <td class="price">
            <a href="${pageContext.servletContext.contextPath}/products/priceHistory/${product.id}">
                <fmt:formatNumber value="${product.price}" type="currency" currencySymbol="${product.currency.symbol}"/>
            </a>
        </td>
    </tr>
</c:forEach>
</table>
</tags:master>
