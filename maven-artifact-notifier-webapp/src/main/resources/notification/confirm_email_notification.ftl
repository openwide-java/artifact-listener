<#ftl strip_text=true>
<#if SUBJECT!false>Email confirmation</#if>

<#if BODY_TEXT!false>
Hello,

${email.user.displayName} has added your email address as an additional notification address on our artifact listener service.

In order to confirm that you are actually interested in these notifications, please click on the following link:
${url}

-- 
artifact listener
</#if>