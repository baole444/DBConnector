package dbConnect.models.enums;

/**
 * Enums responsible for each data model.
 * Must be manually assigned by user.
 * <div>
 *     <i>* These enums are superseded by Table enums system as it can also act as Model enums.</i>
 * </div>
 *
 * @deprecated use {@link Table} enums instead.
 * @since version 1.5
 */
@Deprecated
public enum DataModel {
    Supplier,
    Merchandise,
    MerchandiseCategory,
    Customer,
    ImportBill,
    RetailBill,
    ImportBillItems,
    RetailBillItems,
    GenericModel
}
