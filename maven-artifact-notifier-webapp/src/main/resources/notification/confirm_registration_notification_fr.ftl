<#ftl strip_text=true>
<#if SUBJECT!false>Confirmation d'inscription</#if>

<#if BODY_TEXT!false>
Bonjour,

Vous avez créé le compte ${user.userName} sur notre service artifact listener.

Afin de valider ce compte, veuillez cliquer sur le lien suivant :
${url}

-- 
artifact listener
</#if>