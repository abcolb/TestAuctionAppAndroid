package com.hubspot.auctionapp.models;

import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;
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

  public DatabaseReference itemRef;

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

  public Boolean getIsBiddingOpen() {
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
      return false
    }
    if (isLive) {
      if (now.compare(LIVE_BIDDING_OPENS!) != ComparisonResult.orderedDescending) {
        return false
      }
    } else {
      if (now.compare(BIDDING_OPENS!) != ComparisonResult.orderedDescending) {
        return false
      }
    }
    return true*/
    return true;
  }

  public String getBidStatus() {
    if (!this.getIsBiddingOpen()) {
      return "";
    } else if (false) {
      return "OUTBID!";
    } else if (true) {
      return "WINNING";
    } else {
      return "BID NOW";
    }
  }

  public String getBiddingTimelineString() {
    return "BIDDING OPENS 12/12/2016";
  }

  public Integer getPrice() {
    if (this.bids.size() > 0) {
      return 500; // new Bid(bids.first).getAmount()
    }
    return this.openbid;
  }

  public String getBidSize() {
    Integer currentPrice = this.getPrice();
    if (currentPrice < 50) {
      return "SMALL";
    } else if (currentPrice < 100) {
      return "MEDIUM";
    } return "LARGE";
  }

  //public ArrayList<Integer> getIncrements() {

  /*let BIDDING_INCREMENTS : [String: [Int]] = [
  "SMALL": [1, 5, 10],
  "MEDIUM": [5, 10, 25],
  "LARGE": [10, 25, 50]
  ] */

  //let suggestedIncremements = BIDDING_INCREMENTS[this.getBidType()]
  //return [priceIncrements![0] + self.getPrice(), priceIncrements![1] + self.getPrice(), priceIncrements![2] + self.getPrice()]

}
