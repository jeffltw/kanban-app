<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">

<%@ page pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<html>

<head>
	<title>Project Estimation Tool for Kanban</title>
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/header.css"/>

	<script type="text/javascript">
		function changeProjectProperty(promptName, submitName, oldValue) {
			var value = prompt("Change " + promptName + " to?", oldValue);
			if (value != null && value != "") {
				document.getElementById("projectPropertyNameHiddenField").value = submitName;
				document.getElementById("projectPropertyValueHiddenField").value = value;
				return true;
			}
			return false;
		}
	</script>
	
</head>

<body class="main">
	<h1>Project Estimation Tool for Kanban</h1>

	<p>
		<a href='../..'>Go back to main Kanban site</a> <br/>
		<a href='../../projects/${project.projectName}/wall'>Goto project <b>${project.projectName}</b> wall</a>
	</p>

	<form action="set-project-property">
		<table class="pet">
			<tr>
				<td>Budget</td>
				<td>$ ${project.budget}</td>
				<td>
					<input id="projectPropertyNameHiddenField" type="hidden" name="name" value="" /> 
					<input id="projectPropertyValueHiddenField" type="hidden" name="value" value="" /> 
					<input type="submit" value="Edit" onclick="return changeProjectProperty('budget', 'budget', '${project.budget}');" />
				</td>
			</tr>
			<tr>
				<td>Cost so far <img src="${pageContext.request.contextPath}/images/question.png" title="Please update this field regularly" /></td>
				<td>$ ${project.costSoFar}</td>
				<td><input type="submit" value="Edit" onclick="return changeProjectProperty('cost so far', 'costSoFar', '${project.costSoFar}');" /></td>
			</tr>
			<tr>
				<td>Cost per point (estimated) <img src="${pageContext.request.contextPath}/images/question.png" title="Compare this value with 'Cost per point so far'" /></td>
				<td>$ ${project.estimatedCostPerPoint}</td>
				<td>
					<input type="submit" value="Edit" onclick="return changeProjectProperty('estimated cost per point', 'estimatedCostPerPoint', '${project.estimatedCostPerPoint}');" />
				</td>
			</tr>
			<tr>
				<td>Cost per point so far <img src="${pageContext.request.contextPath}/images/question.png" title="This value is based on Complete features and 'Cost so far'" /></td>
				<td>$ ${project.costPerPointSoFar}</td>
				<td>
				</td>
			</tr>
		</table>
	</form>

	<h2>Planned features</h2>

	<table class="pet">
		<tr>
			<th rowspan="2" colspan="2">Actions</th>
			<th rowspan="2">Description</th>
			<th rowspan="2">Importance</th>
			<th colspan="2">Feature points</th>
			<th colspan="2">Running estimate</th>
		</tr>
		<tr>
			<th>Best Case</th>
			<th>Worst Case</th>
			<th>Best Case</th>
			<th>Worst Case <img src="${pageContext.request.contextPath}/images/question.png" title="Yes, this is not a simple sum of worst cases for features" /></th>
		</tr>
		<c:forEach items="${project.budgetEntries}" var="entry" varStatus="status">
		
			<c:set var="tagClass" value="${entry.feature.workItem.mustHave ? (entry.overBudgetInWorstCase ? 'mustHaveOver' : 'mustHaveOk') : (entry.overBudgetInBestCase ? 'niceHaveOver' : 'niceHaveOk') }" scope="page" />
		
			<tr class="${tagClass}">
				<td>
					<table class="nolines">
						<tr>
							<td colspan="2">
								<form action="set-feature-included-in-estimates">
									<div>
										<input type="hidden" name="id" value="${entry.feature.id}" />

										<input type="hidden" name="value" value="${entry.feature.workItem.mustHave ? 'false' : 'true'}" /> 

										<c:choose>
										 	<c:when test="${entry.canChangeImportance}">
												<input type="submit" value="${entry.feature.workItem.mustHave ? 'Nice to Have' : 'Must Have'}" />
											</c:when>
											<c:otherwise>
												<input type="submit" value="${entry.feature.workItem.mustHave ? 'Nice to Have' : 'Must Have'}" disabled="disabled" title="You must reorder this item to change its priority" />
											</c:otherwise>
										</c:choose>
									</div>
								</form>
							</td>
						</tr>
						<tr>
							<td>
								<form action="edit-feature">
									<div>
										<input type="hidden" name="id" value="${entry.feature.id}" />
										<input type="submit" value="Edit Est." />
									</div>
								</form>
							</td>
						</tr>
					</table>
				</td>
				<td>
					<form action="move-feature">
						<div>
							<input type="hidden" name="id" value="${entry.feature.id}" /> 
							<input type="hidden" name="direction" value="up" /> 

							<c:choose>
								<c:when test="${entry.prevFeature == null}">
									<input type="submit" value="↑" disabled="disabled"/>
								</c:when>
								<c:otherwise>
									<input type="hidden" name="targetId" value="${entry.prevFeature.id}" />
									<input type="submit" value="↑"/>
								</c:otherwise>
							</c:choose>
						</div>
					</form>
					<form action="move-feature">
						<div>
							<input type="hidden" name="id" value="${entry.feature.id}" /> 
							<input type="hidden" name="direction" value="down" /> 
							
							<c:choose>
								<c:when test="${entry.nextFeature == null}">
									<input type="submit" value="↓" disabled="disabled"/>
								</c:when>
								<c:otherwise>
									<input type="hidden" name="targetId" value="${entry.nextFeature.id}" />
									<input type="submit" value="↓" />
								</c:otherwise>
							</c:choose>
						</div>
					</form>
				</td>
				<td class="${tagClass}">${entry.feature.description}</td>
				<td><i>${entry.feature.workItem.mustHave ? 'Must have' : 'Nice to have'}</i></td>
				<td>${entry.feature.workItem.bestCaseEstimate}</td>
				<td>${entry.feature.workItem.worstCaseEstimate}</td>
				<td class="${tagClass}">$ ${entry.bestCaseCumulativeCost}</td>
				<td class="${tagClass}">$ ${entry.worstCaseCumulativeCost}</td>
			</tr>
		</c:forEach>
		<tr>
			<td colspan="2" />
			<td colspan="6">
				<span style="color: #777777">To add more features go to project wall</span>
			</td>
		</tr>
	</table>
	
	<h4>Legend</h4>
	<table class="pet">
		<tr class="mustHaveOk"><td>'Must have' features</td></tr>
		<tr class="mustHaveOver"><td>'Must have' features over the budget (Worst Case)</td></tr>
		<tr class="niceHaveOk"><td>'Nice to have' features</td></tr>
		<tr class="niceHaveOver"><td>'Nice to have' features over the budget (Best Case)</td></tr>
	</table>

	<h2>Complete features</h2>

	<table class="pet">
		<tr>
			<th>Description</th>
			<th>Feature points <img src="${pageContext.request.contextPath}/images/question.png" title="This field shows 'Best Case' estimate for the feature" /></th>
		</tr>
		<c:forEach items="${project.completedFeatures}" var="feature">
			<c:set var="tagClass" value="${feature.workItem.mustHave ? 'mustHaveOk' : 'niceHaveOk' }" scope="page" />

			<tr class="${tagClass}">
				<td>${feature.description}</td>
				<td>${feature.workItem.bestCaseEstimate}</td>
			</tr>
		</c:forEach>
	</table>

	<p>Cost per point so far: $ ${project.costPerPointSoFar}</p>
</body>

</html>
