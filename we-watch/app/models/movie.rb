class Movie < ApplicationRecord
  has_one_attached :cover
  belongs_to :user, foreign_key: 'user_id'

  def cover_url
    Rails.application.routes.url_helpers.url_for(cover) if cover.attached?
  end
end
