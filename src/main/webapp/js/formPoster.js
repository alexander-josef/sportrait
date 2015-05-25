/*simple js to post a form to a certain action
* can be used in combination with a link to either follow the link or post the form
*/
function postForm(actionName,element)
{
	element.forms[0].action=actionName;
	element.forms[0].submit();
	return false;
}

function postValidate(activity,element)
{
	element.form.activity.value=activity;
    element.form.preSubmit(activity, element.form);
	return validate(element.form);
}

function postApplicationForm(activity,application,element)
{
    element.form.application.value=application;
    postForm(activity,element);
}

/**
 * populate a paramater 'actionParam' with the value of actionParamValue and submit the form
 * @param actionParamValue
 * @param formIndex Index of form that shall be postet. Starts with 0!
 */
function postSimpleForm(actionParamValue,formIndex)
{
    var frm = document.forms[formIndex];
    frm.actionParam.value = actionParamValue;
//    alert('posting form with actionParam ' + frm.actionParam.value)
    frm.submit();
    return false;
}

function postActionParam(actionParam,element)
{
	element.form.hugo.value=actionParam;
	element.forms[0].submit();
	return true;
}