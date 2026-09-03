
export interface CreatePortingRequest{
  phoneNumber:string;
}

export interface PortingRequest{
  id:number;
  phoneNumber: string;
  donor: string;
  recipient: string;
  status: string;
  rejectionReason?: string;
  createdAt: string;
  updatedAt: string;
}

export interface MobileNumberStatus {
  phoneNumber: string;
  currentOperator: string;
  ported: boolean;
  activeRequestStatus: string | null;
  lastPortingRequestId: number | null;
}
