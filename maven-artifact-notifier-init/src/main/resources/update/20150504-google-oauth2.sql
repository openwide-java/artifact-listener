begin;

update user_ set email=email || '__OPENID_GOOGLE', username = username || '__OPENID_GOOGLE' where authenticationType='OPENID_GOOGLE';

commit;