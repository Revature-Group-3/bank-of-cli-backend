// the shape of every failure, matching docs/json_format/unsuccessful_responses/
export interface ApiError {
  status: number;
  message: string;
  timestamp: string;
}
