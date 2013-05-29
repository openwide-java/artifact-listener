<#ftl strip_text=true>
<#if SUBJECT!false>Nouvelles versions - ${.now?date?iso_local}</#if>

<#if BODY_TEXT!false>
Bonjour,

De nouvelles versions ont été déployées sur Maven Central :

<#list notifications as notification>
<#assign uniqueId = notification.artifactVersion.artifact.group.groupId + ':' + notification.artifactVersion.artifact.artifactId> 
 * ${uniqueId} - ${notification.artifactVersion.version}
</#list>

Si vous ne souhaitez plus recevoir de notifications par mail, vous pouvez vous désabonner à l'adresse suivante :
${unsubscribeUrl}

-- 
artifact listener
</#if>