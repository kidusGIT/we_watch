Rails.application.routes.draw do
  default_url_options host: '192.168.8.148', port: 3000

  # rails s -b 192.168.129.202 to change the ip address

  # default_url_options host: 'localhost', port: 3000
  resources :movies
  get 'user-movies/', to: 'movies#user_movies'

  post 'auth/', to: 'user#sign_up'
  get 'auth/user', to: 'user#active_user'
  put 'auth/edit', to: 'user#edit_user'
  post 'auth/login', to: 'user#login'

end
