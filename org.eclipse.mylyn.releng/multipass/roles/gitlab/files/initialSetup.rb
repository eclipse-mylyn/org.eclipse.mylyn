## root personal acces token creation
token = User.find_by_username('root').personal_access_tokens.create(
	name: 'apitoken',
	token_digest: Gitlab::CryptoHelper.sha256('glpat-mylyn45Test123'),
	impersonation: false,
	scopes: [:api],
	expires_at: 365.days.from_now)
token.save!

## Create aditional users

#random_password = ::User.random_password
random_password = SecureRandom.hex.slice(0, 16)
ua = User.new(username: 'mylynAdmin',
    email: 'mylynadmin@eclipse.org',
    name: 'Mylyn Admin User',
    confirmed_at: DateTime.now,
    password: random_password,
    admin: true)
ua.assign_personal_namespace(Organizations::Organization.default_organization)                                       
ua.skip_confirmation!
ua.save!

puts '==========================================================='
puts "INFO: Password for newly created ua is: #{random_password}"
puts '==========================================================='

tokena = ua.personal_access_tokens.create(
	name: 'apitoken',
	token_digest: Gitlab::CryptoHelper.sha256('glpat-Adm1nPwdTok123'),
	impersonation: false,
	scopes: [:api],
	expires_at: 365.days.from_now)
tokena.save!


random_password = SecureRandom.hex.slice(0, 16)

nt = Namespace.new(name: 'mylynTest', 
    description: 'mylynTest namespace')
ut = User.new(username: 'mylynTest',
    email: 'mylyntest@eclipse.org',
    name: 'Mylyn Test User',
    confirmed_at: DateTime.now,
    password: random_password,
    admin: true)
ut.assign_personal_namespace(Organizations::Organization.default_organization)                                      
ut.skip_confirmation!
ut.save!

puts '==========================================================='
puts "INFO: Password for newly created ut is: #{random_password}"
puts '==========================================================='

tokent = ut.personal_access_tokens.create(
	name: 'apitoken',
	token_digest: Gitlab::CryptoHelper.sha256('glpat-Test1nPwd12345'),
	impersonation: false,
	scopes: [:api],
	expires_at: 365.days.from_now)
tokent.save!
ut.save