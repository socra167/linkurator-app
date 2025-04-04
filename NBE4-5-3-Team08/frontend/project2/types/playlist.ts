// member 인터페이스 추가
export interface Member {
  id: number;
  username?: string;
  profileImage?: string;
}

export interface PlaylistItem {
  id: number;
  itemId: number;
  itemType: "LINK" | "CURATION";
  displayOrder: number;
  url: string;
  title: string;
  thumbnailUrl?: string;
  creator?: string;
  description?: string;
}

export interface Playlist {
  id: number;
  title: string;
  description?: string;
  thumbnailUrl?: string;
  isPublic: boolean;
  createdAt: string;
  items?: PlaylistItem[];
  tags?: string[];
  viewCount: number;
  likeCount: number;
  member?: Member;
  owner?: boolean;
  public?: boolean;
}

export interface LinkData {
  title: string;
  url: string;
  thumbnailUrl?: string;
  creator?: string;
  description?: string;
}
