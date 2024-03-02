class UserController < ApplicationController
  rescue_from ActiveRecord::RecordInvalid, with: :handle_invalid_record

  def sign_up
    @user = User.create(user_params)
    if @user.save
      @token = encode_token(user_id: @user.id)
      render json: { user: @user, token: @token}, status: :created
    else
      render json: @user.errors, status: :unprocessable_entity
    end
  end

  def edit_user
    @user = current_user
    if @user.update_columns(full_name: user_params['full_name'], username: user_params['username'], bio: user_params['bio'])
      puts @user.full_name
      render json: @user
    else
      puts @user.errors.full_messages
      render json: { message: "error" }
    end
  end

  def active_user
    @user = current_user
    # render json: @user
    if !@user
      render json: { username: "Error username" }
    else
      render json: @user
    end
  end

  def login
    begin
      @user = User.find_by!(username: user_params[:username])
      if @user.authenticate(user_params[:password])
        @token = encode_token(user_id: @user.id)
        render json: { user: @user, token: @token }, status: :accepted
      else
        render json: {message: 'Incorrect password'}, status: :ok
      end
    rescue ActiveRecord::RecordNotFound
      render json: {message: 'Invalid username'}, status: :ok
    end
  end

  private
  def user_params
    params.permit(:full_name, :username, :password, :bio)
  end

  def handle_invalid_record(e)
    render json: { errors: e.record.errors.full_messages }, status: :unprocessable_entity
  end
end
