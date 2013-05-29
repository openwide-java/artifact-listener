<#ftl strip_text=true>
<#if SUBJECT!false>Confirmation</#if>

<#if BODY_TEXT!false>
Bonjour,

${email.user.displayName} a ajouté votre adresse email comme adresse de notification supplémentaire sur notre service artifact listener.

Afin de confirmer que vous êtes effectivement intéressé par ces notifications, veuillez cliquer sur le lien suivant :
${url}

-- 
artifact listener
</#if>