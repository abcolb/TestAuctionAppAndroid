package com.hubspot.auctionapp.models;

import java.util.Map;
import java.util.HashMap;

public class Item {

  public String name;
  public Integer openbid;
  public String donoremail;
  public String imageurl;
  public Integer qty;
  public String donorname;
  public String description;
  public Integer islive;
  public Map<String, Boolean> bids = new HashMap<>();

  public Item() {
    // Default constructor required for calls to DataSnapshot.getValue(Item.class)
  }

  public String getName() {
    return name;
  }

  public String getImageurl() {
    return this.imageurl == null || this.imageurl.isEmpty() ? "http://cdn2.hubspot.net/hubfs/53/sprocket_web-2.png" : this.imageurl;
  }

  public Integer getQty() {
    return qty;
  }

  public String getDonorname() {
    return donorname;
  }

  public String getDescription() {
    return description;
  }

  public Boolean getIslive() {
    return islive == 1;
  }

  public Integer getNumBids() {
    return bids.size();
  }

  public String getWinningBidsString() {
    return "$50, $51, $52";
  }

  public String getBidStatus() {
    /*let formatter = DateFormatter()
    formatter.dateFormat = "yyyy/MM/dd HH:mm"
    // SMOKE TEST DATA
    // let BIDDING_OPENS = formatter.date(from: "2016/12/12 15:00")
    // let BIDDING_CLOSES = formatter.date(from: "2016/12/14 20:00")
    // let LIVE_BIDDING_OPENS = formatter.date(from: "2016/12/14 17:00")

    // LIVE AUCTION DATA
    // let BIDDING_OPENS = formatter.date(from: "2016/12/12 15:00")
    // let BIDDING_CLOSES = formatter.date(from: "2016/12/14 20:00")
    // let LIVE_BIDDING_OPENS = formatter.date(from: "2016/12/14 17:00")

    let BIDDING_OPENS = formatter.date(from: "2016/11/12 15:00")
    let BIDDING_CLOSES = formatter.date(from: "2016/12/14 20:00")
    let LIVE_BIDDING_OPENS = formatter.date(from: "2016/12/14 17:00")

    let now = NSDate()

    if (now.compare(BIDDING_CLOSES!) == ComparisonResult.orderedDescending) {
      cell.bidNowButton.isHidden = true
    }
    if (item.isLive) {
      if (now.compare(LIVE_BIDDING_OPENS!) != ComparisonResult.orderedDescending) {
        cell.bidNowButton.isHidden = true
      }
    } else {
      if (now.compare(BIDDING_OPENS!) != ComparisonResult.orderedDescending) {
        cell.bidNowButton.isHidden = true
      }
    }*/

    /*if (!item.getIslive()) {
      ((TextView) view.findViewById(R.id.item_detail)).setText(getString(R.string.bid_now));
    } else {
      ((TextView) view.findViewById(R.id.item_detail)).s;
    }*/
    if (!this.getIslive()){
      return "BID NOW"; // "WINNING", "OUTBID!"
    }
    return "";
  }
}
