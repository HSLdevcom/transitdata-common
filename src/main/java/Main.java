public class Main {
    public static void main(String[] args) {

        String query = new StringBuilder()
                .append("SELECT ")
                .append("   DISTINCT CONVERT(CHAR(16), DVJ.Id) AS dvd_id, ")
                .append("   KVV.StringValue AS route, ")
                .append("   SUBSTRING(CONVERT(CHAR(16), VJT.IsWorkedOnDirectionOfLineGid), 12, 1) AS direction, ")
                .append("   CONVERT(CHAR(8), DVJ.OperatingDayDate, 112) AS operating_day, ")
                .append("   RIGHT('0' + (CONVERT(VARCHAR(2), (DATEDIFF(HOUR, '1900-01-01', PlannedStartOffsetDateTime)))), 2) ")
                .append("       + ':' + RIGHT('0' + CONVERT(VARCHAR(2), ((DATEDIFF(MINUTE, '1900-01-01', PlannedStartOffsetDateTime)) ")
                .append("       - ((DATEDIFF(HOUR, '1900-01-01', PlannedStartOffsetDateTime) * 60)))), 2) + ':00' AS start_time, ")
                .append("   CONVERT(CHAR(7), JPP.Number) AS stop_number ")
                .append("FROM ptDOI4_Community.dbo.DatedVehicleJourney AS DVJ ")
                .append("LEFT JOIN ptDOI4_Community.dbo.VehicleJourney AS VJ ON (DVJ.IsBasedOnVehicleJourneyId = VJ.Id) ")
                .append("LEFT JOIN ptDOI4_Community.dbo.VehicleJourneyTemplate AS VJT ON (DVJ.IsBasedOnVehicleJourneyTemplateId = VJT.Id) ")
                .append("LEFT JOIN ptDOI4_Community.T.KeyVariantValue AS KVV ON (KVV.IsForObjectId = VJ.Id) ")
                .append("LEFT JOIN ptDOI4_Community.dbo.KeyVariantType AS KVT ON (KVT.Id = KVV.IsOfKeyVariantTypeId) ")
                .append("LEFT JOIN ptDOI4_Community.dbo.KeyType AS KT ON (KT.Id = KVT.IsForKeyTypeId) ")
                .append("LEFT JOIN ptDOI4_Community.dbo.ObjectType AS OT ON (KT.ExtendsObjectTypeNumber = OT.Number) ")
                .append("LEFT JOIN ptDOI4_Community.dbo.JourneyPatternPoint AS JPP ON (VJT.StartsAtJourneyPatternPointGid = JPP.Gid) ")
                .append("WHERE ")
                .append("   ( ")
                .append("       KT.Name = 'JoreIdentity' ")
                .append("       OR KT.Name = 'JoreRouteIdentity' ")
                .append("       OR KT.Name = 'RouteName' ")
                .append("   ) ")
                .append("   AND OT.Name = 'VehicleJourney' ")
                .append("   AND VJT.IsWorkedOnDirectionOfLineGid IS NOT NULL ")
                .append("   AND DVJ.OperatingDayDate >= '2020-07-15' ")
                .append("   AND DVJ.OperatingDayDate < '2020-07-16' ")
                .append("   AND DVJ.IsReplacedById IS NULL ")
                .append("   AND VJT.TransportModeCode = 'METRO' ")
                .toString();
        System.out.println(query);
    }
}
