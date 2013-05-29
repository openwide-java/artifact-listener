<#ftl strip_text=true>
<#if SUBJECT!false>New releases - ${.now?date?iso_local}</#if>

<#if BODY_TEXT!false>
Hello,

New releases have been deployed on Maven Central:

<#list notifications as notification>
<#assign uniqueId = notification.artifactVersion.artifact.group.groupId + ':' + notification.artifactVersion.artifact.artifactId> 
 * ${uniqueId} - ${notification.artifactVersion.version}
</#list>

If you want to configure your notifications, sign in and change your settings on:
${unsubscribeUrl}

-- 
artifact listener
</#if>