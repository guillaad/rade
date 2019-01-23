<%/*
 *  This file is part of the Rade project (https://github.com/mgimpel/rade).
 *  Copyright (C) 2018 Marc Gimpel
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */%>
<%/* $Id$ */%>
<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<jsp:include page="aesn_header.jsp" />
<table style="margin-left:auto;margin-right:auto;">
	<tr>
		<td>OriginalFileName: </td>
		<td>${file.originalFilename}</td>
	</tr>
	<tr>
		<td>Type:</td>
		<td>${file.contentType}</td>
	</tr>
	<tr>
		<td>Name:</td>
		<td>${file.name}</td>
	</tr>
	<tr>
		<td>Size:</td>
		<td>${file.size}</td>
	</tr>
	<tr>
		<td>URI:</td>
		<td>${uri}</td>
	</tr>
</table>
<jsp:include page="aesn_footer.jsp" />