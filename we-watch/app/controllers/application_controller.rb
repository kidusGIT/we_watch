class ApplicationController < ActionController::API

  def encode_token(payload)
    JWT.encode(payload, set_secret_key)
  end

  def decode_token
    header = request.headers['Token']
    # puts header
    if header
      token = header.split(' ')[1]
      begin
        JWT.decode(token, set_secret_key)
      rescue JWT::DecodeError
        nil
      end
    end
  end

  def current_user
    @data = decode_token
    if @data
      user_id = @data[0]['user_id']
      @user = User.find_by(id: user_id)
    else
      false
    end
  end

  def authorized?
    if !current_user
      render json: { message: 'Please log in' }, status: :unauthorized
    else
      true
    end
  end

  private
    def set_secret_key
      'fldkjrejop0423j32032jlksdj9243023fjwejlkvsjlkvsv'
    end

end
