<#ftl strip_text=true>
<#if SUBJECT!false>Account registration confirmation</#if>

<#if BODY_TEXT!false>
Hello,

You have created the account ${user.userName} on our artifact listener service.

In order to validate your account, please click on the following link:
${url}

-- 
artifact listener
</#if>