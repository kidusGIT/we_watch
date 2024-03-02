class MoviesController < ApplicationController
  before_action :set_movie, only: %i[ show update destroy ]
  before_action :authorized?, only: %i[ create update destroy user_movies ]

  # GET /movies
  def index
    @movies = Movie.all

    render json: @movies, methods: :cover_url
  end

  # GET /movies/1
  def show
    render json: @movie, methods: :cover_url
  end

  # GET /user-movies
  def user_movies
    @movies = Movie.where(user_id: current_user.id)
    render json: @movies, methods: :cover_url
  end

  # POST /movies
  def create
    @movie = Movie.new(movie_params)

    if current_user.id.to_i != movie_params[:user_id].to_i
      render json: { message: 'Invalid user' }
    else
      if @movie.save
        render json: @movie,  methods: :cover_url, status: :created, location: @movie
      else
        render json: @movie.errors, status: :unprocessable_entity
      end
    end
  end

  # PATCH/PUT /movies/1
  def update
    if @movie.update(movie_params)
      render json: @movie, methods: :cover_url
    else
      render json: @movie.errors, status: :unprocessable_entity
    end
  end

  # DELETE /movies/1
  def destroy
    @movie.destroy!
    render json: { deleted: true }
  end

  private
    # Use callbacks to share common setup or constraints between actions.
    def set_movie
      @movie = Movie.find(params[:id])
    end

    # Only allow a list of trusted parameters through.
    def movie_params
      params.permit(:title, :user_id, :description, :cover)
    end
end
