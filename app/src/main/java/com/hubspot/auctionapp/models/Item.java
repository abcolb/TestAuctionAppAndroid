/*
 * Copyright (c) 2016 Razeware LLC
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.hubspot.auctionapp.models;

import java.io.IOException;
import java.net.URL;
import java.util.Map;

public class Item {

  public String id;
  public String key;
  public String name;
  public Integer openbid;
  public String donoremail;
  public String imageurl;
  public Integer qty;
  public String donorname;
  public String description;
  public Integer islive;
  // public Map<String, Object> bids;
  public String ref;

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
    return donorname;
  }

  public Boolean getIslive() {
    return islive == 1;
  }

  public String getKey() {
    return key;
  }

  public String getRef() {
    return ref;
  }

  public String getId() {
    return id;
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
    return "BID NOW"; // "WINNING", "OUTBID!"
  }
}
